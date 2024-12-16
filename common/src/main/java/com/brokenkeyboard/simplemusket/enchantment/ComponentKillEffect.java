package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record ComponentKillEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<ComponentKillEffect> CODEC = MapCodec.unit(new ComponentKillEffect());

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3) {
        if (entity instanceof LivingEntity living && living.isDeadOrDying() && living.getLastDamageSource() != null) {
            boolean bonus = living.getLastDamageSource().getDirectEntity() instanceof BulletEntity bullet && bullet.getBullet().equals(ModRegistry.ENCHANTED_CARTRIDGE);
            item.itemStack().set(ModRegistry.AMMO_BONUS, bonus ? 1 : 0);
        }
    }

    @Override
    public MapCodec<ComponentKillEffect> codec() {
        return CODEC;
    }
}