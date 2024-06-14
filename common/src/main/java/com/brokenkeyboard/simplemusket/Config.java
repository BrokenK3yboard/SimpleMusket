package com.brokenkeyboard.simplemusket;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue RELOAD_TIME;
    public static ModConfigSpec.IntValue AIM_TIME;
    public static ModConfigSpec.BooleanValue CRAFT_HELLFIRE_CARTRIDGE;
    public static ModConfigSpec.BooleanValue FIND_HELLFIRE_CARTRIDGE;
    public static ModConfigSpec.BooleanValue BARTER_HELLFIRE_CARTRIDGE;
    public static ModConfigSpec.BooleanValue REDUCE_MOB_DAMAGE;
    public static ModConfigSpec.DoubleValue GUNSLINGER_RANGE;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        registerConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {

        RELOAD_TIME = builder
                .comment("The number of ticks needed to reload a musket. 20 ticks = 1 second.")
                .defineInRange("Musket reload time", 40, 40, 80);

        AIM_TIME = builder
                .comment("The number of ticks needed to aim a musket. 20 ticks = 1 second.")
                .defineInRange("Musket aim time", 60, 40, 80);

        CRAFT_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges are craftable.")
                .define("Craftable Hellfire cartridges", false);

        FIND_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges can be found in chest loot.")
                .define("Hellfire cartridges", true);

        BARTER_HELLFIRE_CARTRIDGE = builder
                .comment("If enabled, hellfire cartridges can be obtained by bartering with piglins")
                .define("Barter Hellfire cartridges", true);

        REDUCE_MOB_DAMAGE = builder
                .comment("If enabled, the amount of damage dealt by mobs armed with muskets is reduced by 25%.")
                .define("Reduced Pillager bullet damage", false);

        GUNSLINGER_RANGE = builder
                .comment("Intended for use with the Enhanced AI mod. Any value above the default is not recommended otherwise.")
                .defineInRange("Gunslinger Pillager range", 24D, 24D, 40D);
    }
}