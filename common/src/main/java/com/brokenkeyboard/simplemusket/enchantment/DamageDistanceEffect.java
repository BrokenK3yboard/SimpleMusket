package com.brokenkeyboard.simplemusket.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record DamageDistanceEffect(float bonus, LevelBasedValue value) {

    public static final Codec<DamageDistanceEffect> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ExtraCodecs.POSITIVE_FLOAT.fieldOf("base_damage_bonus").forGetter(DamageDistanceEffect::bonus),
                    LevelBasedValue.CODEC.fieldOf("level_damage_multiplier").forGetter(DamageDistanceEffect::value))
            .apply(instance, DamageDistanceEffect::new));

    public float process(int level, Entity entity, DamageSource source, float damage) {
        if (!(source.getEntity() instanceof LivingEntity living)) return damage;
        double distance = Math.min(living.position().distanceTo(entity.position()), 48);
        double coef = Math.max(((0.1953125D * bonus * distance * distance) - (3.125D * bonus * distance) + (bonus * 100)) * value.calculate(level) / 100, 0);
        return damage * (float) (ModEnchantments.isEnchantedBullet(source) ? 1 + coef * 1.2 : 1 + coef);
    }
}