package com.brokenkeyboard.simplemusket.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface IPlatformHelper {

    boolean isModLoaded(String modId);
    void playSound(SoundSource source, ServerLevel level, Vec3 origin);
    Entity getHitEntity(Entity entity);
    Holder<MobEffect> createEffectHolder(String name, MobEffect effect);
}