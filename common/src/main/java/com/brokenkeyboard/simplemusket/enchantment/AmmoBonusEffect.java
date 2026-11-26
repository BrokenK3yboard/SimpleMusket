package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record AmmoBonusEffect(LevelBasedValue ammoBonus) implements EnchantmentEntityEffect {

    public static final MapCodec<AmmoBonusEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(LevelBasedValue.CODEC.fieldOf("ammo_bonus").forGetter(AmmoBonusEffect::ammoBonus))
                    .apply(instance, AmmoBonusEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 vec3) {
        if (entity instanceof LivingEntity living && living.isDeadOrDying() && living.getLastDamageSource() != null) {
            item.itemStack().set(ModRegistry.AMMO_BONUS.value(), (int) ammoBonus.calculate(enchantmentLevel));
        }
    }

    @Override
    public MapCodec<AmmoBonusEffect> codec() {
        return CODEC;
    }
}