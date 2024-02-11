package com.brokenkeyboard.simplemusket.platform.services;

import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface IPlatformHelper {

    boolean isModLoaded(String modId);
    boolean bulletHitResult(BulletEntity entity, HitResult result);
    void playSound(SoundEvent sound, SoundSource source, ServerLevel level, Vec3 origin);
    EnchantmentCategory musketCategory();
    Entity getHitEntity(Entity entity);
}