package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Objects;

public class MusketAttackGoal<T extends Mob> extends Goal {

    private static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T MOB;
    private final float SPEED_MODIFIER;
    private final float ATTACK_RANGE;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public MusketAttackGoal(T mob, float speedModifier, float attackRange) {
        this.MOB = mob;
        this.SPEED_MODIFIER = speedModifier;
        this.ATTACK_RANGE = attackRange * attackRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected boolean isHoldingMusket() {
        return MOB.isHolding(stack -> stack.getItem() instanceof MusketItem);
    }

    protected boolean isValidTarget() {
        return MOB.getTarget() != null && MOB.getTarget().isAlive();
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingMusket() && !(MOB instanceof MusketPillager && MOB.distanceToSqr(Objects.requireNonNull(MOB.getTarget())) <= 100);
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !MOB.getNavigation().isDone()) && this.isHoldingMusket();
    }

    @Override
    public void stop() {
        super.stop();
        MOB.setAggressive(false);
        MOB.setTarget(null);
        seeTime = 0;
        if (MOB.isUsingItem()) {
            MOB.stopUsingItem();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.MOB.getTarget();
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.MOB, ModRegistry.MUSKET);
        ItemStack stack = MOB.getItemInHand(hand);

        if (target != null && MOB.getItemInHand(hand).getItem() instanceof MusketItem musket) {
            boolean hasLOS = MOB.getSensing().hasLineOfSight(target);
            boolean seeTarget = seeTime > 0;

            if (hasLOS != seeTarget) {
                seeTime = 0;
            }

            seeTime = (hasLOS ? seeTime + 1 : seeTime - 1);

            double distance = MOB.distanceToSqr(target);
            boolean moving = (distance > (double) ATTACK_RANGE || seeTime < 5) && attackDelay == 0;

            if (moving) {
                if (--updatePathDelay <= 0) {
                    MOB.getNavigation().moveTo(target, SPEED_MODIFIER);
                    updatePathDelay = PATHFINDING_DELAY_RANGE.sample(MOB.getRandom());
                }
            } else {
                updatePathDelay = 0;
                MOB.getNavigation().stop();
            }

            MOB.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!MusketItem.isLoaded(stack) && !MOB.isUsingItem()) {
                MOB.startUsingItem(hand);
            } else if (!MusketItem.isLoaded(stack) && MOB.isUsingItem() && MOB.getTicksUsingItem() > Config.RELOAD_TIME.get()) {
                MOB.releaseUsingItem();
                attackDelay = 50 + MOB.getRandom().nextInt(30);
            } else if (MusketItem.isLoaded(stack)) {
                if (attackDelay > 0) {
                    --attackDelay;
                } else if (hasLOS && seeTime > Config.AIM_TIME.get()) {
                    MOB.level().registryAccess().registry(Registries.ENCHANTMENT);
                    float velocity = ((BulletItem) MusketItem.getLoadedAmmo(stack).getItem()).VELOCITY;
                    float deviation = (float) (8 - MOB.level().getDifficulty().getId() * 2);
                    musket.fire(MOB.level(), MOB, hand, stack, velocity, deviation, MOB.getTarget(), SoundSource.HOSTILE);
                }
            }
        }
    }
}