package com.brokenkeyboard.simplemusket.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RepeatingEnchantment extends Enchantment {

    public RepeatingEnchantment(EnchantmentCategory category, EquipmentSlot... slots) {
        super(Rarity.VERY_RARE, category, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }
}