package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityFabricMixin {

    @WrapOperation(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(FFF)F"))
    private float applyArmorPiercing(float damage, float armor, float toughness, Operation<Float> original, @Local(argsOnly = true) DamageSource source) {
        return source.is(Constants.BULLET) ? BulletEntity.applyArmorPiercing(damage, armor, toughness, (BulletEntity)source.getDirectEntity()) : original.call(damage, armor, toughness);
    }
}
