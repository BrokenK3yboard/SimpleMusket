package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonModel.class)
public class SkeletonModelMixin<T extends Mob & RangedAttackMob> {

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/Mob;FFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/model/HumanoidModel;prepareMobModel(Lnet/minecraft/world/entity/LivingEntity;FFF)V"))
    private void setArmAnimation(T mob, float limbSwing, float limbSwingAmount, float partialTick, CallbackInfo ci, @Local ItemStack stack) {
        if (stack.is(ModRegistry.MUSKET) && mob.isAggressive()) {
            if (MusketItem.isLoaded(stack)) {
                if (mob.getMainArm() == HumanoidArm.RIGHT) {
                    ((SkeletonModel<?>) (Object) this).rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                } else {
                    ((SkeletonModel<?>) (Object) this).leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
            } else if (mob.isUsingItem()) {
                if (mob.getMainArm() == HumanoidArm.RIGHT) {
                    ((SkeletonModel<?>) (Object) this).rightArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                } else {
                    ((SkeletonModel<?>) (Object) this).leftArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            }
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;getMainHandItem()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER),
            cancellable = true)
    private void setupAnim(CallbackInfo cir, @Local(argsOnly = true) T mob) {
        if (mob.getMainHandItem().is(ModRegistry.MUSKET)) {
            cir.cancel();
        }
    }
}
