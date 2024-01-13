package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.FirearmItem;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class FirearmAttackGoal extends Goal {

    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final MusketPillager mob;
    private final float speedModifier;
    private final float attackRange;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public FirearmAttackGoal(MusketPillager mob, float speedModifier, float attackRange) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackRange = attackRange * attackRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean isHoldingFirearm() {
        return this.mob.isHolding(is -> is.getItem() instanceof FirearmItem);
    }

    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingFirearm();
    }

    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingFirearm();
    }

    private boolean isValidTarget() {
        return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setTarget(null);
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            this.mob.setReloading(false);
        }
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof FirearmItem);
            ItemStack stack = mob.getItemInHand(hand);
            boolean hasLOS = this.mob.getSensing().hasLineOfSight(target);
            boolean seeTarget = this.seeTime > 0;

            if (hasLOS != seeTarget) {
                this.seeTime = 0;
            }

            if (hasLOS) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            double distance = this.mob.distanceToSqr(target);
            boolean moving = (distance > (double) this.attackRange || this.seeTime < 5) && this.attackDelay == 0;
            if (moving) {
                --this.updatePathDelay;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().moveTo(target, this.speedModifier);
                    this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!FirearmItem.hasAmmo(stack) && !moving) {
                this.mob.startUsingItem(hand);
                this.mob.setReloading(true);
            } else if (FirearmItem.hasAmmo(stack) && !FirearmItem.isLoaded(stack) && this.mob.isUsingItem()) {
                this.mob.releaseUsingItem();
                FirearmItem.setLoaded(stack, true);
                this.mob.setReloading(false);
                this.attackDelay = 50 + this.mob.getRandom().nextInt(30);
            } else if (FirearmItem.hasAmmo(stack) && FirearmItem.isLoaded(stack)) {
                if (this.attackDelay > 0) {
                    --this.attackDelay;
                } else if (hasLOS) {
                    this.mob.useFirearm(stack);
                }
            }
        }
    }
}