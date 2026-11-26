package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {

    @WrapOperation(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float setBarProgress(LocalPlayer player, float v, Operation<Float> original) {
        ItemStack stack = player.getUseItem();
        return MusketItem.isLoaded(stack)
                ? Math.clamp(((float) stack.getUseDuration(player) - player.getUseItemRemainingTicks()) / Config.AIM_TIME.get(), 0, 1)
                : original.call(player, v);
    }
}
