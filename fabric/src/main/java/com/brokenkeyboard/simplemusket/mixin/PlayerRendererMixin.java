package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof MusketItem) {
            if (MusketItem.hasAmmo(stack)) {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
                cir.cancel();
            } else if (player.isUsingItem() && !MusketItem.hasAmmo(stack)) {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                cir.cancel();
            }
        }
    }
}