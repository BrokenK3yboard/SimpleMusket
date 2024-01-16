package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BulletItem extends Item {

    public final double DAMAGE;
    public final double PIERCING;

    public BulletItem(double damage, double piercing, Properties properties) {
        super(properties);
        this.DAMAGE = damage;
        this.PIERCING = piercing;
    }

    public Component getName(ItemStack stack) {
        if (!stack.is(SimpleMusket.IRON_BULLET.get())) return super.getName(stack);
        return (Config.CRAFT_COPPER_BULLETS.get() || Config.CRAFT_GOLD_BULLETS.get() || Config.CRAFT_NETHERITE_BULLETS.get() || Config.FIND_NETHERITE_BULLETS.get()) ?
                super.getName(stack) : new TranslatableComponent("item." + SimpleMusket.MOD_ID + ".generic_bullet");
    }
}