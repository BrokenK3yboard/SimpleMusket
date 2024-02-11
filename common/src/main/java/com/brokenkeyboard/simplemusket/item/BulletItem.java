package com.brokenkeyboard.simplemusket.item;

import net.minecraft.world.item.Item;

public class BulletItem extends Item {

    public final double DAMAGE;
    public final double PIERCING;

    public BulletItem(double damage, double piercing) {
        super(new Item.Properties());
        this.DAMAGE = damage;
        this.PIERCING = piercing;
    }
}