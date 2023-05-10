package com.brokenkeyboard.simplemusket;

public enum BulletType {
    NONE(0, 0, 0),
    IRON(16.0F, 0.5, 40),
    COPPER(4.0F, 0, 20),
    GOLD(9.0F, 0, 30),
    NETHERITE(20.0F, 1.0, 40);

    private final float damage;
    private final double piercing;
    private final int lifespan;

    BulletType(float damage, double piercing, int lifespan) {
        this.damage = damage;
        this.piercing = piercing;
        this.lifespan = lifespan;
    }

    public float getDamage() {
        return damage;
    }

    public double getPiercing() {
        return piercing;
    }

    public int getLifespan() {
        return lifespan;
    }
}