package com.brokenkeyboard.simplemusket;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue RELOAD_TIME;
    public static ModConfigSpec.IntValue AIM_TIME;
    public static ModConfigSpec.DoubleValue BULLET_DAMAGE;
    public static ModConfigSpec.DoubleValue MOB_DAMAGE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SKELETON_MUSKET_SPAWN_CHANCE;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        registerConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {

        RELOAD_TIME = builder
                .comment("The number of ticks needed to reload a musket. 20 ticks = 1 second.")
                .defineInRange("Musket reload time", 30, 20, 80);

        AIM_TIME = builder
                .comment("The number of ticks needed to aim a musket. 20 ticks = 1 second.")
                .defineInRange("Musket aim time", 30, 20, 80);

        BULLET_DAMAGE = builder
                .comment("The amount of damage dealt by bullets.")
                .defineInRange("Bullet damage", 16D, 16D, 24D);

        MOB_DAMAGE_MULTIPLIER = builder
                .comment("Damage multiplier applied to bullets fired by mobs.")
                .defineInRange("Mob bullet damage multiplier", 0.45D, 0.45D, 1.0D);

        SKELETON_MUSKET_SPAWN_CHANCE = builder
                .comment("Chance for a skeleton to spawn with a musket instead of a bow.")
                .defineInRange("Skeleton musket spawn chance", 0.05D, 0D, 1D);
    }
}