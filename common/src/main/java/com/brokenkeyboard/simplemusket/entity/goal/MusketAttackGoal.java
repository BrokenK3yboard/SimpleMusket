package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class MusketAttackGoal extends Goal {

    private static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final MusketPillager MOB;
    private final float SPEED_MODIFIER;
    private final float ATTACK_RANGE;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public MusketAttackGoal(MusketPillager mob, float speedModifier, float attackRange) {
        this.MOB = mob;
        this.SPEED_MODIFIER = speedModifier;
        this.ATTACK_RANGE = attackRange * attackRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected boolean isHoldingMusket() {
        return MOB.isHolding(is -> is.getItem() instanceof MusketItem);
    }

    public boolean canUse() {
        return isValidTarget() && isHoldingMusket() && !MOB.isUsingSawnOff();
    }

    public boolean canContinueToUse() {
        return isValidTarget() && (canUse() || !MOB.getNavigation().isDone()) && this.isHoldingMusket() && !MOB.isUsingSawnOff();
    }

    protected boolean isValidTarget() {
        return MOB.getTarget() != null && MOB.getTarget().isAlive();
    }

    public void stop() {
        super.stop();
        MOB.setAggressive(false);
        MOB.setTarget(null);
        seeTime = 0;
        if (MOB.isUsingItem()) {
            MOB.stopUsingItem();
            MOB.setReloading(false);
        }
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.MOB.getTarget();

        if (target != null) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.MOB, ModRegistry.MUSKET);
            ItemStack stack = MOB.getItemInHand(hand);
            boolean hasLOS = MOB.getSensing().hasLineOfSight(target);
            boolean seeTarget = seeTime > 0;

            if (hasLOS != seeTarget) {
                seeTime = 0;
            }

            if (hasLOS) {
                ++seeTime;
            } else {
                --seeTime;
            }

            double distance = MOB.distanceToSqr(target);
            boolean moving = (distance > (double) ATTACK_RANGE || seeTime < 5) && attackDelay == 0;
            if (moving) {
                --updatePathDelay;
                if (updatePathDelay <= 0) {
                    MOB.getNavigation().moveTo(target, SPEED_MODIFIER);
                    updatePathDelay = PATHFINDING_DELAY_RANGE.sample(MOB.getRandom());
                }
            } else {
                updatePathDelay = 0;
                MOB.getNavigation().stop();
            }

            MOB.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!MusketItem.hasAmmo(stack) && !moving) {
                MOB.startUsingItem(hand);
                MOB.setReloading(true);
            } else if (MusketItem.hasAmmo(stack) && !MusketItem.isLoaded(stack) && MOB.isUsingItem()) {
                MOB.releaseUsingItem();
                MOB.setReloading(false);
                attackDelay = 50 + MOB.getRandom().nextInt(30);
            } else if (MusketItem.hasAmmo(stack) && MusketItem.isLoaded(stack)) {
                if (attackDelay > 0) {
                    --attackDelay;
                } else if (hasLOS && MOB.getAttackCD() <= 0) {
                    MOB.useMusket(stack);
                }
            }
        }
    }
}