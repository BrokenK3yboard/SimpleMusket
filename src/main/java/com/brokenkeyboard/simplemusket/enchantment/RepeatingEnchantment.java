package com.brokenkeyboard.simplemusket.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import static com.brokenkeyboard.simplemusket.SimpleMusket.FIREARM;

public class RepeatingEnchantment extends Enchantment {
    public RepeatingEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, FIREARM, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }
}