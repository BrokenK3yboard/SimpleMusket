package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
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

public class MusketAttackGoal<T extends Mob> extends Goal {

    private static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T mob;
    private final float speedModifier;
    private final float attackRangeSqr;
    private final float minimumDistanceSqr;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public MusketAttackGoal(T mob, float speedModifier, float attackRange, float minimumDistance) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackRangeSqr = attackRange * attackRange;
        this.minimumDistanceSqr = minimumDistance * minimumDistance;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected boolean isHoldingMusket() {
        return mob.isHolding(stack -> stack.getItem() instanceof MusketItem);
    }

    protected boolean isValidTarget() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && mob.distanceToSqr(target) >= minimumDistanceSqr;
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingMusket();
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !mob.getNavigation().isDone()) && this.isHoldingMusket();
    }

    @Override
    public void start() {
        super.start();
        mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        mob.setAggressive(false);
        mob.setTarget(null);
        seeTime = 0;
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.mob, ModRegistry.MUSKET);
        ItemStack stack = mob.getItemInHand(hand);

        if (target != null && mob.getItemInHand(hand).getItem() instanceof MusketItem musket) {
            boolean hasLOS = mob.getSensing().hasLineOfSight(target);
            boolean seeTarget = seeTime > 0;

            if (hasLOS != seeTarget) {
                seeTime = 0;
            }

            seeTime = (hasLOS ? seeTime + 1 : seeTime - 1);

            double distance = mob.distanceToSqr(target);
            boolean moving = (distance > (double) attackRangeSqr || seeTime < 5) && attackDelay == 0;

            if (moving) {
                if (--updatePathDelay <= 0) {
                    mob.getNavigation().moveTo(target, speedModifier);
                    updatePathDelay = PATHFINDING_DELAY_RANGE.sample(mob.getRandom());
                }
            } else {
                updatePathDelay = 0;
                mob.getNavigation().stop();
            }

            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!MusketItem.isLoaded(stack) && !mob.isUsingItem()) {
                mob.startUsingItem(hand);
            } else if (!MusketItem.isLoaded(stack) && mob.isUsingItem() && mob.getTicksUsingItem() > Config.RELOAD_TIME.get()) {
                mob.releaseUsingItem();
                attackDelay = 50 + mob.getRandom().nextInt(30);
            } else if (MusketItem.isLoaded(stack)) {
                if (attackDelay > 0) {
                    --attackDelay;
                } else if (hasLOS && seeTime > Config.AIM_TIME.get()) {
                    mob.level().registryAccess().registry(Registries.ENCHANTMENT);
                    float velocity = ((BulletItem) MusketItem.getLoadedAmmo(stack).getItem()).velocity;
                    float deviation = (float) (8 - mob.level().getDifficulty().getId() * 2);
                    musket.fire(mob.level(), mob, hand, stack, velocity, deviation, mob.getTarget(), SoundSource.HOSTILE);
                }
            }
        }
    }
}