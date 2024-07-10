package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float setBarProgress(LocalPlayer player, float baseTime) {
        ItemStack stack = player.getUseItem();
        return stack.getItem() instanceof MusketItem musket && MusketItem.isLoaded(stack)
                ? MusketItem.getUsePerc(player, stack, musket.getUseDuration(stack, player) - player.getUseItemRemainingTicks())
                : player.getAttackStrengthScale(baseTime);
    }

    @Redirect(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float setHotbarProgress(LocalPlayer player, float baseTime) {
        ItemStack stack = player.getUseItem();
        return stack.getItem() instanceof MusketItem musket && MusketItem.isLoaded(stack)
                ? MusketItem.getUsePerc(player, stack, musket.getUseDuration(stack, player) - player.getUseItemRemainingTicks())
                : player.getAttackStrengthScale(baseTime);
    }
}
