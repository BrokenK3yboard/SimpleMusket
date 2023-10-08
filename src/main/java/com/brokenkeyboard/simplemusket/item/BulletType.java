package com.brokenkeyboard.simplemusket.item;

public enum BulletType {
    NONE(0, 0, 0),
    IRON(16.0F, 0.5, 40),
    COPPER(4.0F, 0, 20),
    GOLD(9.0F, 0, 30),
    NETHERITE(20.0F, 1.0, 40);

    public final float DAMAGE;
    public final double PIERCING;
    public final int LIFESPAN;

    BulletType(float damage, double piercing, int lifespan) {
        this.DAMAGE = damage;
        this.PIERCING = piercing;
        this.LIFESPAN = lifespan;
    }
}