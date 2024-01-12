package com.brokenkeyboard.simplemusket.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import static com.brokenkeyboard.simplemusket.SimpleMusket.FIREARM;

public class DeadeyeEnchantment extends Enchantment {
    public DeadeyeEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, FIREARM, slots);
    }

    public int getMinCost(int level) {
        return 12 + (level - 1) * 20;
    }

    public int getMaxCost(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 3;
    }
}
