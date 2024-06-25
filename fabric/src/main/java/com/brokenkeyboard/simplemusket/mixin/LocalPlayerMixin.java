package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Shadow public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void modifySpeed(CallbackInfo ci) {
        Player player = ((Player) (Object)this);
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());

        if (stack.getItem() instanceof MusketItem && MusketItem.isLoaded(stack) && player.isUsingItem()) {
            Optional<Registry<Enchantment>> registry = player.level().registryAccess().registry(Registries.ENCHANTMENT);
            if (registry.isPresent()) {
                int deadeye = EnchantmentHelper.getItemEnchantmentLevel(registry.get().getHolderOrThrow(ModRegistry.DEADEYE), stack);
                if (deadeye > 0) {
                    float multiplier = 2 + deadeye;
                    this.input.leftImpulse *= multiplier;
                    this.input.forwardImpulse *= multiplier;
                }
            }
        }
    }
}