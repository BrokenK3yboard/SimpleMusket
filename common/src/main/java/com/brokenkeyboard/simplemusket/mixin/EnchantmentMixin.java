package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "modifyDamageFilteredValue", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void modifyEnch(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> $$0, ServerLevel serverLevel, int level, ItemStack stack, Entity entity,
                            DamageSource source, MutableFloat mutableFloat, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalIntRef value) {
        if (entity instanceof LivingEntity living && living.hasEffect(ModRegistry.HEX_EFFECT)) {
            value.set(value.get() + 2);
        }
    }
}