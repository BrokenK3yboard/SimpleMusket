package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.world.InteractionHand;
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
    private void setArmAnimation(T entity, float limbSwing, float limbSwingAmount, float partialTick, CallbackInfo ci) {
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.is(ModRegistry.MUSKET) && entity.isAggressive()) {
            if (MusketItem.isLoaded(stack)) {
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
                    ((SkeletonModel<?>) (Object) this).rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                } else {
                    ((SkeletonModel<?>) (Object) this).leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
            } else if (entity.isUsingItem()) {
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
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
    private void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity.getMainHandItem().is(ModRegistry.MUSKET)) {
            ci.cancel();
        }
    }
}
