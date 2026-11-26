package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
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
    private static void modifyArmor(ServerLevel serverLevel, ItemStack itemStack, Entity victim, DamageSource damageSource, float armorFraction, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(ModRegistry.BULLET)) {
            cir.setReturnValue((float) (cir.getReturnValue() - 0.1));
        }
    }
}