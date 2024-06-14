package com.brokenkeyboard.simplemusket;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "simplemusket";
	public static final String MOD_NAME = "Simple Musket";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final ResourceLocation BASTION_OTHER = ResourceLocation.withDefaultNamespace("chests/bastion_other");
	public static final ResourceLocation HOGLIN_STABLE = ResourceLocation.withDefaultNamespace("chests/bastion_hoglin_stable");
	public static final ResourceLocation BASTION_TREASURE = ResourceLocation.withDefaultNamespace("chests/bastion_treasure");
	public static final ResourceLocation BASTION_BRIDGE = ResourceLocation.withDefaultNamespace("chests/bastion_bridge");
	public static final ResourceLocation PIGLIN_BARTER = ResourceLocation.withDefaultNamespace("gameplay/piglin_bartering");

	public static final ResourceLocation CRAFT_CARTRIDGE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_cartridge");
	public static final ResourceLocation CRAFT_ENCHANTED = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_enchanted");
	public static final ResourceLocation CRAFT_HELLFIRE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_hellfire");

	public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bullet"));
}