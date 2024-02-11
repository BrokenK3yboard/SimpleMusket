package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "evaluateWhichHandsToRender", at = @At(value = "RETURN"), cancellable = true)
    private static void renderHands(LocalPlayer player, CallbackInfoReturnable<ItemInHandRenderer.HandRenderSelection> cir) {
        InteractionHand hand = player.getUsedItemHand();
        if (player.getItemInHand(hand).getItem() instanceof MusketItem && player.isUsingItem()) {
            cir.setReturnValue(ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY);
        }
    }
}