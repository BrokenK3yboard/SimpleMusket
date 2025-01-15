package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class SawnOffGoal extends Goal {

    protected final MusketPillager MOB;
    protected final float ATTACK_RANGE;
    protected int attackDelay;

    public SawnOffGoal(MusketPillager mob, float attackRange) {
        this.MOB = mob;
        this.ATTACK_RANGE = attackRange * attackRange;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void stop() {
        super.stop();
        MOB.setUsingSawnOff(false);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = MOB.getTarget();
        return target != null && target.isAlive() && MOB.distanceToSqr(target) <= ATTACK_RANGE && MOB.isHolding(is -> is.getItem() instanceof MusketItem) && MOB.getSawnoffCD() <= 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = MOB.getTarget();

        if (target != null && MOB.getSawnoffCD() <= 0) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.MOB, ModRegistry.MUSKET);
            ItemStack stack = MOB.getItemInHand(hand);
            boolean hasLOS = MOB.getSensing().hasLineOfSight(target);

            this.MOB.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!this.MOB.isUsingSawnOff()) {
                MOB.setUsingSawnOff(true);
                attackDelay = 20 + MOB.getRandom().nextInt(30);
            } else {
                if (this.attackDelay > 0) {
                    --this.attackDelay;
                } else if (hasLOS && stack.getItem() instanceof MusketItem musket) {
                    if (!MusketItem.isLoaded(stack)) {
                        MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, 1));
                    }
                    float deviation = (float) (8 - MOB.level().getDifficulty().getId() * 2);
                    float velocity = ((BulletItem) MusketItem.getLoadedAmmo(stack).getItem()).VELOCITY;
                    musket.fire(MOB.level(), MOB, hand, stack, velocity, deviation, MOB.getTarget(), SoundSource.HOSTILE);
                    MOB.setSawnoffCD(160);
                    MOB.setUsingSawnOff(false);
                    int amount = this.MOB.level() instanceof ServerLevel ? MusketItem.getAmmoCount(stack) : 1;
                    MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, amount));
                }
            }
        }
    }
}