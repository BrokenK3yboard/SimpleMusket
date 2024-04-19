package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

    protected boolean isHoldingMusket() {
        return this.MOB.isHolding(is -> is.getItem() instanceof MusketItem);
    }

    @Override
    public void stop() {
        super.stop();
        MOB.setUsingSawnOff(false);
    }

    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingMusket() && MOB.getSawnoffCD() <= 0;
    }

    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.MOB.getNavigation().isDone()) && this.isHoldingMusket() && MOB.getSawnoffCD() <= 0;
    }

    protected boolean isValidTarget() {
        LivingEntity target = MOB.getTarget();
        return target != null && target.isAlive() && MOB.distanceToSqr(target) <= ATTACK_RANGE;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = MOB.getTarget();

        if (target != null && MOB.getSawnoffCD() <= 0) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.MOB, ModRegistry.MUSKET);
            ItemStack stack = MOB.getItemInHand(hand);
            boolean hasLOS = MOB.getSensing().hasLineOfSight(target);

            this.MOB.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!this.MOB.isUsingSawnOff()) {
                MOB.setUsingSawnOff(true);
                attackDelay = 20;
            } else {
                if (this.attackDelay > 0) {
                    --this.attackDelay;
                } else if (hasLOS) {
                    if (!MusketItem.hasAmmo(stack)) {
                        MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, 1));
                    }
                    if (!MusketItem.isLoaded(stack)) {
                        MusketItem.setLoaded(stack, true);
                    }
                    MOB.useMusket(stack);
                    MOB.setSawnoffCD(120);
                    MOB.setAttackCD(20);
                    MOB.setUsingSawnOff(false);
                    MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.REPEATING, stack) + 1));
                    MusketItem.setLoaded(stack, true);
                }
            }
        }
    }
}