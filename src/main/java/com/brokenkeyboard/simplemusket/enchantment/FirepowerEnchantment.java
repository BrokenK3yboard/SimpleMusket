package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import static com.brokenkeyboard.simplemusket.SimpleMusket.FIREARM;

public class FirepowerEnchantment extends Enchantment {

    public FirepowerEnchantment(Rarity rarity, EquipmentSlot... slots) {
        super(rarity, FIREARM, slots);
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

    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != SimpleMusket.LONGSHOT.get();
    }
}