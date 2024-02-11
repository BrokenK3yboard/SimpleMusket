package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class FirepowerEnchantment extends Enchantment {

    public FirepowerEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    public int getMinCost(int level) {
        return 1 + (level - 1) * 10;
    }

    public int getMaxCost(int level) {
        return this.getMinCost(level) + 15;
    }

    public int getMaxLevel() {
        return 5;
    }

    public boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != ModRegistry.LONGSHOT.get() && ench != ModRegistry.BLAST.get();
    }
}