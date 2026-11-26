package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AvatarRenderer.class)
public class PlayerRendererMixin {

    @WrapOperation(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean getArmPose(ItemStack itemStack, Object o, Operation<Boolean> original) {
        if (!original.call(itemStack, o)) {
            return itemStack.is(ModRegistry.MUSKET.value());
        }
        return false;
    }
}