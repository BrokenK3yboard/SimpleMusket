package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.FirearmItem;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Objects;

public class FirearmAttackGoal extends Goal {

    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private FirearmState firearmState = FirearmState.UNLOADED;
    private final MusketPillager mob;
    private final float speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public FirearmAttackGoal(MusketPillager mob, float speedModifier, float attackRadiusSqr) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackRadiusSqr = attackRadiusSqr * attackRadiusSqr;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingFirearm();
    }

    private boolean isHoldingFirearm() {
        return this.mob.isHolding(is -> is.getItem() instanceof FirearmItem);
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
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            double d0 = this.mob.distanceToSqr(livingentity);
            boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
            if (flag2) {
                --this.updatePathDelay;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
                    this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            if (this.firearmState == FirearmState.UNLOADED) {
                if (!flag2) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof FirearmItem));
                    this.firearmState = FirearmState.LOADING;
                    this.mob.setReloading(true);
                }
            } else if (this.firearmState == FirearmState.LOADING) {
                if (!this.mob.isUsingItem()) {
                    this.firearmState = FirearmState.UNLOADED;
                }

                int i = this.mob.getTicksUsingItem();
                ItemStack stack = this.mob.getUseItem();
                if (stack.getItem() instanceof FirearmItem firearm && i >= firearm.getReload(stack)) {
                    this.mob.releaseUsingItem();
                    this.firearmState = FirearmState.LOADED;
                    this.attackDelay = 50 + this.mob.getRandom().nextInt(30);
                    this.mob.setReloading(false);
                    FirearmItem.setAmmoType(stack, 1);
                    FirearmItem.setReady(stack, true);
                }
            } else if (this.firearmState == FirearmState.LOADED) {
                --this.attackDelay;
                if (this.attackDelay == 0) {
                    this.firearmState = FirearmState.READY;
                }
            } else if (this.firearmState == FirearmState.READY && flag) {
                ItemStack stack = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof FirearmItem));
                if(stack.getItem() instanceof FirearmItem) {
                    LivingEntity target = this.mob.getTarget();
                    double armor = (target.getAttributes().hasAttribute(Attributes.ARMOR) ? Objects.requireNonNull(target.getAttribute(Attributes.ARMOR)).getValue() : 0);
                    if(d0 < 100 && armor < 7.5F)
                        FirearmItem.setAmmoType(stack, 2);
                    this.mob.useFirearm(stack);
                    FirearmItem.setAmmoType(stack, 0);
                    FirearmItem.setReady(stack, false);
                }
                this.firearmState = FirearmState.UNLOADED;
            }
        }
    }

    private boolean canRun() {
        return this.firearmState == FirearmState.UNLOADED;
    }

    enum FirearmState {
        UNLOADED,
        LOADING,
        LOADED,
        READY
    }
}