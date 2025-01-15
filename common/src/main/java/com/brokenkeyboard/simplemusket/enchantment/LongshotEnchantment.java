package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class LongshotEnchantment extends Enchantment {

    public LongshotEnchantment(EnchantmentCategory category, EquipmentSlot... slots) {
        super(Rarity.RARE, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != ModRegistry.FIREPOWER && ench != ModRegistry.REPEATING;
    }
}