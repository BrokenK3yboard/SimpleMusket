package com.brokenkeyboard.simplemusket.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(DamageSources.class)
public interface DamageSourcesInvoker {

    @Invoker("source")
    DamageSource invokeSource(ResourceKey<DamageType> damageTypeKey, @Nullable Entity causingEntity, @Nullable Entity directEntity);
}