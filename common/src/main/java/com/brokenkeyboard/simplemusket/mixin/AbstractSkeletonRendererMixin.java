package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeletonRenderer.class)
public class AbstractSkeletonRendererMixin {

    @ModifyReturnValue(method = "getArmPose(Lnet/minecraft/world/entity/monster/skeleton/AbstractSkeleton;Lnet/minecraft/world/entity/HumanoidArm;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("RETURN"))
    private HumanoidModel.ArmPose getArmPose(HumanoidModel.ArmPose original, @Local(argsOnly = true, name = "mob") AbstractSkeleton skeleton, @Local(argsOnly = true, name = "arm") HumanoidArm arm) {
        if (skeleton.getMainArm() == arm && skeleton.isAggressive()) {
            ItemStack itemStack = skeleton.getMainHandItem();
            if (itemStack.is(ModRegistry.MUSKET.value())) {
                if (MusketItem.isLoaded(itemStack) || Services.PLATFORM.isMobUsingBayonet(skeleton)) {
                    return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                } else if (skeleton.isUsingItem() && ItemStack.isSameItem(itemStack, skeleton.getUseItem())) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            }
        }
        return original;
    }
}
