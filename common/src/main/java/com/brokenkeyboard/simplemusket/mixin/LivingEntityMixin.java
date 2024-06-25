package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"))
    private float modifyEnchantmentPower(float damage, float protection) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(ModRegistry.HEX_EFFECT)) {
            float value = Mth.clamp(protection, 0.0F, 20.0F) / 2;
            return damage * (1.0F - value / 25.0F);
        }
        return CombatRules.getDamageAfterMagicAbsorb(damage, protection);
    }
}
