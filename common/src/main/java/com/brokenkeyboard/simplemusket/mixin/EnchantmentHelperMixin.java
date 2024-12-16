package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "modifyArmorEffectiveness", at = @At("RETURN"), cancellable = true)
    private static void modifyArmor(ServerLevel level, ItemStack stack, Entity entity, DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.is(ModRegistry.BULLET)) {
            int firepower = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.FIREPOWER), stack);
            boolean boost = firepower > 0 && ModEnchantments.isEnchantedBullet(source);
            cir.setReturnValue((float) (cir.getReturnValue() - (boost ? 0.2 : 0.1)));
        }
    }
}