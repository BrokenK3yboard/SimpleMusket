package com.brokenkeyboard.simplemusket;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "simplemusket";
	public static final String MOD_NAME = "Simple Musket";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final ResourceLocation BASTION_OTHER = ResourceLocation.withDefaultNamespace("chests/bastion_other");
	public static final ResourceLocation BASTION_TREASURE = ResourceLocation.withDefaultNamespace("chests/bastion_treasure");
	public static final ResourceLocation BASTION_BRIDGE = ResourceLocation.withDefaultNamespace("chests/bastion_bridge");
	public static final ResourceLocation PIGLIN_BARTER = ResourceLocation.withDefaultNamespace("gameplay/piglin_bartering");
}