package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class ModEnchantments {

    public static float modifyDamageDistance(ServerLevel level, ItemStack stack, Entity entity, DamageSource source, float damage) {
        MutableFloat mutablefloat = new MutableFloat(damage);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchantmentLevel) -> enchantment.value().getEffects(ModRegistry.DAMAGE_DISTANCE).forEach(
                effect -> mutablefloat.setValue(effect.effect().process(enchantmentLevel, entity, source, mutablefloat.getValue()))));
        return mutablefloat.floatValue();
    }
}
