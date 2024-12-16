package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class ModEnchantments {

    public static float modifyDamageDistance(ItemStack stack, Entity entity, DamageSource source, float damage) {
        MutableFloat mutablefloat = new MutableFloat(damage);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) -> enchantment.value().getEffects(ModRegistry.DAMAGE_DISTANCE)
                .forEach(effect -> mutablefloat.setValue(effect.effect().process(enchantmentLevel, entity, source, mutablefloat.getValue()))));
        return mutablefloat.floatValue();
    }

    public static int modifyAmmoCount(ItemStack stack, float ammoCount) {
        MutableFloat mutableFloat = new MutableFloat(ammoCount);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) -> enchantment.value().getEffects(ModRegistry.AMMO_COUNT)
                .forEach(effect -> mutableFloat.setValue(effect.effect().process(enchantmentLevel, stack, ammoCount))));
        return Math.max(0, mutableFloat.intValue());
    }

    public static boolean isEnchantedBullet(DamageSource source) {
        return source.is(ModRegistry.BULLET) && source.getDirectEntity() instanceof BulletEntity bullet
                && bullet.getBullet().equals(ModRegistry.ENCHANTED_CARTRIDGE);
    }
}
