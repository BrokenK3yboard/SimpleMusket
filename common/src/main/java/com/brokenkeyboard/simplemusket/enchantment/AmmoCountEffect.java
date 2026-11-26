package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record AmmoCountEffect(LevelBasedValue base) {

    public static final Codec<AmmoCountEffect> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("base_increase").forGetter(AmmoCountEffect::base))
            .apply(instance, AmmoCountEffect::new));

    public float process(int level, ItemStack weapon, float ammoCount) {
        int bonus = Optional.ofNullable(weapon.get(ModRegistry.AMMO_BONUS.value())).orElse(0);
        weapon.remove(ModRegistry.AMMO_BONUS.value());
        return ammoCount + base.calculate(level) + bonus;
    }
}