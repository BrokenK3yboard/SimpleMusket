package com.brokenkeyboard.simplemusket;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    public static final String MOD_ID = "simplemusket";
    public static final String MOD_NAME = "Simple Musket";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final ResourceLocation BASTION_OTHER = new ResourceLocation("minecraft:chests/bastion_other");
    public static final ResourceLocation BASTION_TREASURE = new ResourceLocation("minecraft:chests/bastion_treasure");
    public static final ResourceLocation BASTION_BRIDGE = new ResourceLocation("minecraft:chests/bastion_bridge");
    public static final ResourceLocation PIGLIN_BARTER = new ResourceLocation("minecraft:gameplay/piglin_bartering");

    public static final ResourceLocation CRAFT_CARTRIDGE = new ResourceLocation(Constants.MOD_ID, "craft_cartridge");
    public static final ResourceLocation CRAFT_ENCHANTED = new ResourceLocation(Constants.MOD_ID, "craft_enchanted");
    public static final ResourceLocation CRAFT_HELLFIRE = new ResourceLocation(Constants.MOD_ID, "craft_hellfire");

    public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MOD_ID, "bullet"));
    public static final DamageType BULLET_DAMAGE_TYPE = new DamageType("bullet", DamageScaling.NEVER, 0.5F);
}