package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FirepowerEnchantment extends Enchantment {

    public FirepowerEnchantment(EnchantmentCategory category, EquipmentSlot... slots) {
        super(Rarity.COMMON, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != ModRegistry.LONGSHOT && ench != ModRegistry.REPEATING;
    }
}