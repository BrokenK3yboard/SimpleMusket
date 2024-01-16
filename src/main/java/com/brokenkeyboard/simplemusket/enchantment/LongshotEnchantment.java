package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import static com.brokenkeyboard.simplemusket.SimpleMusket.FIREARM;

public class LongshotEnchantment extends Enchantment {
    public LongshotEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, FIREARM, slots);
    }

    public int getMinCost(int level) {
        return 10 + 20 * (level - 1);
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != SimpleMusket.FIREPOWER.get();
    }
}