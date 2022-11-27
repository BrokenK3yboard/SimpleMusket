package com.brokenkeyboard.simplemusket;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

    public static ForgeConfigSpec.IntValue MUSKET_RELOAD_TIME;
    public static ForgeConfigSpec.IntValue MUSKET_AIM_TIME;

    public static ForgeConfigSpec.BooleanValue CRAFT_COPPER_BULLETS;
    public static ForgeConfigSpec.BooleanValue CRAFT_GOLD_BULLETS;
    public static ForgeConfigSpec.BooleanValue CRAFT_NETHERITE_BULLETS;

    public static ForgeConfigSpec.BooleanValue CONSECRATION_COMPAT;
    public static ForgeConfigSpec.BooleanValue FIND_NETHERITE_BULLETS;

    public static void registerConfig() {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();

        MUSKET_RELOAD_TIME = CONFIG_BUILDER
                .comment("The number of ticks needed to reload a musket. 20 ticks = 1 second.")
                .defineInRange("Musket reload time", 50, 25, 100);

        MUSKET_AIM_TIME = CONFIG_BUILDER
                .comment("The number of ticks needed to aim a musket. 20 ticks = 1 second.")
                .defineInRange("Musket aim time", 50, 25, 100);

        CRAFT_COPPER_BULLETS = CONFIG_BUILDER
                .comment("If enabled, copper bullets are craftable.")
                .define("Copper bullets", true);

        CRAFT_GOLD_BULLETS = CONFIG_BUILDER
                .comment("If enabled, gold bullets are craftable.")
                .define("Gold bullets", true);

        CRAFT_NETHERITE_BULLETS = CONFIG_BUILDER
                .comment("If enabled, netherite bullets are craftable.")
                .define("Craftable netherite bullets", false);

        CONSECRATION_COMPAT = CONFIG_BUILDER
                .comment("If enabled while consecration is installed, change golden bullet behavior to remove undead protection")
                .define("Gold bullet damage bonus", true);

        FIND_NETHERITE_BULLETS = CONFIG_BUILDER
                .comment("If enabled, netherite bullets can be found in chest loot.")
                .define("Netherite bullets", true);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_BUILDER.build());
    }
}