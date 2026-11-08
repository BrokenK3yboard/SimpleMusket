package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.goal.BayonetAttackGoal;
import com.brokenkeyboard.simplemusket.entity.goal.MusketAttackGoal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
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
    private final MusketAttackGoal<AbstractSkeleton> simpleMusket$musketGoal = new MusketAttackGoal<>(simpleMusket$skeleton, 1F, 24F, 6F);

    @Unique
    private final BayonetAttackGoal simplemusket$bayonetGoal = new BayonetAttackGoal(simpleMusket$skeleton, 1.2F, false, 6F);

    @Inject(method = "reassessWeaponGoal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;removeGoal(Lnet/minecraft/world/entity/ai/goal/Goal;)V", shift = At.Shift.AFTER))
    private void removeWeaponGoal(CallbackInfo ci) {
        ((MobAccessor) this).getGoalSelector().removeGoal(simplemusket$bayonetGoal);
        ((MobAccessor) this).getGoalSelector().removeGoal(simpleMusket$musketGoal);
    }

    @WrapOperation(method = "reassessWeaponGoal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 1))
    private void changeWeaponGoal(GoalSelector goalSelector, int priority, Goal goal, Operation<Void> original) {
        AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
        boolean useMusketGoal = skeleton.getItemInHand(ProjectileUtil.getWeaponHoldingHand(skeleton, ModRegistry.MUSKET)).is(ModRegistry.MUSKET);

        if (useMusketGoal) {
            original.call(goalSelector, priority, simplemusket$bayonetGoal);
            original.call(goalSelector, priority, simpleMusket$musketGoal);
        } else {
            original.call(goalSelector, priority, goal);
        }
    }

    @WrapOperation(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void changeSpawnWeapon(AbstractSkeleton skeleton, EquipmentSlot slot, ItemStack stack, Operation<Void> original) {
        original.call(skeleton, slot,
                stack.is(Items.BOW) && skeleton.level().getRandom().nextDouble() < Config.SKELETON_MUSKET_SPAWN_CHANCE.get() ? new ItemStack(ModRegistry.MUSKET) : stack);
    }

    @Inject(method = "canFireProjectileWeapon", at = @At(value = "RETURN"), cancellable = true)
    private void canFireProjectileWeapon(ProjectileWeaponItem projectileWeapon, CallbackInfoReturnable<Boolean> cir) {
        if (projectileWeapon == ModRegistry.MUSKET) {
            LivingEntity target = simpleMusket$skeleton.getTarget();
            cir.setReturnValue(target != null && simpleMusket$skeleton.distanceToSqr(target) < 36F);
        }
    }
}
