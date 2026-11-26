package com.brokenkeyboard.simplemusket.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.level.Level;

public class SkeletonMusketAttackGoal extends MusketAttackGoal<AbstractSkeleton> {

    public SkeletonMusketAttackGoal(AbstractSkeleton mob, float speedModifier, float attackRange) {
        super(mob, speedModifier, attackRange);
    }

    @Override
    protected boolean isValidTarget() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && canUseMusketAttack(mob, target);
    }

    public static boolean canUseMusketAttack(Mob mob, LivingEntity target) {
        Level level = mob.level();
        boolean isOutsideMinimumRange = mob.distanceToSqr(target) >= 36F;
        BlockPos roundedPos = BlockPos.containing(target.getX(), target.getEyeY(), target.getZ());
        boolean targetInSunlight = level.dimensionType().hasSkyLight() && level.isBrightOutside() && level.canSeeSky(roundedPos) && mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        return isOutsideMinimumRange || targetInSunlight;
    }
}
