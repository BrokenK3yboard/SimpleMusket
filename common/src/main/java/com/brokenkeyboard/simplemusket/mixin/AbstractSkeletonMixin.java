package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.goal.SkeletonBayonetAttackGoal;
import com.brokenkeyboard.simplemusket.entity.goal.SkeletonMusketAttackGoal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonMixin {

    @Unique
    private final AbstractSkeleton simpleMusket$skeleton = ((AbstractSkeleton) (Object) this);

    @Unique
    private final SkeletonMusketAttackGoal simpleMusket$musketGoal = new SkeletonMusketAttackGoal(simpleMusket$skeleton, 1F, 24F);

    @Unique
    private final SkeletonBayonetAttackGoal simplemusket$bayonetGoal = new SkeletonBayonetAttackGoal(simpleMusket$skeleton, 1.2F, false);

    @Inject(method = "reassessWeaponGoal", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;removeGoal(Lnet/minecraft/world/entity/ai/goal/Goal;)V", shift = At.Shift.AFTER))
    private void removeWeaponGoal(CallbackInfo ci) {
        ((MobAccessor) this).getGoalSelector().removeGoal(simplemusket$bayonetGoal);
        ((MobAccessor) this).getGoalSelector().removeGoal(simpleMusket$musketGoal);
    }

    @WrapOperation(method = "reassessWeaponGoal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 1))
    private void changeWeaponGoal(GoalSelector goalSelector, int prio, Goal goal, Operation<Void> original) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        boolean useMusketGoal = skeleton.getItemInHand(ProjectileUtil.getWeaponHoldingHand(skeleton, ModRegistry.MUSKET.value())).is(ModRegistry.MUSKET.value());

        if (useMusketGoal) {
            original.call(goalSelector, prio, simplemusket$bayonetGoal);
            original.call(goalSelector, prio, simpleMusket$musketGoal);
        } else {
            original.call(goalSelector, prio, goal);
        }
    }

    @WrapOperation(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/skeleton/AbstractSkeleton;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void changeSpawnWeapon(AbstractSkeleton skeleton, EquipmentSlot slot, ItemStack stack, Operation<Void> original, @Local(argsOnly = true, name = "random") RandomSource randomSource) {
        original.call(skeleton, slot, stack.is(Items.BOW) && randomSource.nextDouble() < Config.SKELETON_MUSKET_SPAWN_CHANCE.getAsDouble() ? new ItemStack(ModRegistry.MUSKET.value()) : stack);
    }

    @Inject(method = "canUseNonMeleeWeapon", at = @At(value = "RETURN"), cancellable = true)
    private void canUseNonMeleeWeapon(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (item.is(ModRegistry.MUSKET.value())) {
            LivingEntity target = simpleMusket$skeleton.getTarget();
            cir.setReturnValue(target != null && SkeletonMusketAttackGoal.canUseMusketAttack(simpleMusket$skeleton, target));
        }
    }
}
