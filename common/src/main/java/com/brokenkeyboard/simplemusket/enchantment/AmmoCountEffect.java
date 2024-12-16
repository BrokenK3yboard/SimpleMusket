package com.brokenkeyboard.simplemusket.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import static com.brokenkeyboard.simplemusket.ModRegistry.AMMO_BONUS;

public record AmmoCountEffect(LevelBasedValue base, LevelBasedValue bonus) {

    public static final Codec<AmmoCountEffect> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(LevelBasedValue.CODEC.fieldOf("base_increase").forGetter(AmmoCountEffect::base),
                    LevelBasedValue.CODEC.fieldOf("bonus_increase").forGetter(AmmoCountEffect::bonus))
            .apply(instance, AmmoCountEffect::new));

    public float process(int level, ItemStack weapon, float ammoCount) {
        if (weapon.has(AMMO_BONUS)) {
            int value = weapon.getOrDefault(AMMO_BONUS, 0);
            weapon.remove(AMMO_BONUS);
            return ammoCount + base.calculate(level) + bonus.calculate(level) + value;
        }
        return ammoCount + base.calculate(level);
    }
}