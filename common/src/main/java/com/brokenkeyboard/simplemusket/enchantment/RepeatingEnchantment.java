package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RepeatingEnchantment extends Enchantment {

    public RepeatingEnchantment(EnchantmentCategory category, EquipmentSlot... slots) {
        super(Rarity.VERY_RARE, category, slots);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != ModRegistry.FIREPOWER && ench != ModRegistry.LONGSHOT;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (target instanceof LivingEntity victim) {
            DamageSource source = victim.getLastDamageSource();
            if (victim.isDeadOrDying() && source != null && source.getDirectEntity() instanceof BulletEntity bullet && bullet.getWeapon() != null) {
                bullet.getWeapon().getOrCreateTag().putInt("AmmoBonus", bullet.getBullet().equals(ModRegistry.ENCHANTED_CARTRIDGE) ? 2 : 1);
            }
        }
    }
}