package com.brokenkeyboard.simplemusket;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BulletItem extends Item {

    private final int type;

    public BulletItem(Properties properties, int type) {
        super(properties);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public Component getName(ItemStack stack) {
        if (BulletType.values()[this.getType()] != BulletType.IRON) return super.getName(stack);
        if (Config.CRAFT_COPPER_BULLETS.get() || Config.CRAFT_GOLD_BULLETS.get() || Config.CRAFT_NETHERITE_BULLETS.get() || Config.FIND_NETHERITE_BULLETS.get()) return super.getName(stack);
        return Component.translatable("item." + SimpleMusket.MOD_ID + ".generic_bullet");
    }
}