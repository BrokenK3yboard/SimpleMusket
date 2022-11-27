package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class LongshotEnchantment extends Enchantment {
    public LongshotEnchantment(Rarity rarity, EnchantmentCategory type, EquipmentSlot... slots) {
        super(rarity, type, slots);
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

    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != SimpleMusket.FIREPOWER.get();
    }
}
