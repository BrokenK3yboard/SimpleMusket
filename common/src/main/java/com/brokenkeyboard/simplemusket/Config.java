package com.brokenkeyboard.simplemusket;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.IntValue RELOAD_TIME;
    public static ForgeConfigSpec.IntValue AIM_TIME;
    public static ForgeConfigSpec.BooleanValue CRAFT_ENCHANTED_CARTRIDGE;
    public static ForgeConfigSpec.BooleanValue CRAFT_HELLFIRE_CARTRIDGE;
    public static ForgeConfigSpec.BooleanValue FIND_HELLFIRE_CARTRIDGE;
    public static ForgeConfigSpec.BooleanValue BARTER_HELLFIRE_CARTRIDGE;
    public static ForgeConfigSpec.DoubleValue BULLET_DAMAGE;
    public static ForgeConfigSpec.DoubleValue MOB_DAMAGE_MULT;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        registerConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    public static void registerConfig(ForgeConfigSpec.Builder builder) {

        RELOAD_TIME = builder
                .comment("The number of ticks needed to reload a musket. 20 ticks = 1 second.")
                .defineInRange("Musket reload time", 30, 20, 80);

        AIM_TIME = builder
                .comment("The number of ticks needed to aim a musket. 20 ticks = 1 second.")
                .defineInRange("Musket aim time", 30, 20, 80);

        BULLET_DAMAGE = builder
                .comment("The amount of damage dealt by bullets.")
                .defineInRange("Bullet damage", 16D, 16D, 24D);

        MOB_DAMAGE_MULT = builder
                .comment("Damage multiplier applied to bullets fired by mobs.")
                .defineInRange("Mob bullet damage multiplier", 0.45D, 0.45D, 1.0D);

        CRAFT_ENCHANTED_CARTRIDGE = builder
                .comment("If enabled, enchanted cartridges are craftable.")
                .define("Craftable Enchanted cartridges", true);

        CRAFT_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges are craftable.")
                .define("Craftable Hellfire cartridges", false);

        FIND_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges can be found in chest loot.")
                .define("Hellfire cartridges", true);

        BARTER_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges can be obtained by bartering with piglins")
                .define("Barter Hellfire cartridges", true);
    }
}