package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.network.Network;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

import static com.brokenkeyboard.simplemusket.ModRegistry.CONSECRATION;
import static com.brokenkeyboard.simplemusket.ModRegistry.MUSKET_PILLAGER;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> MUSKET_PILLAGER_EGG = ITEMS.register("musket_pillager_spawn_egg", () -> new ForgeSpawnEggItem(MUSKET_PILLAGER, 5258034, 2960169, new Item.Properties()));
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);

    public SimpleMusket() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.bootstrap();
        ITEMS.register(bus);
        GLM.register(bus);

        ForgeRegistries.ITEMS.addAlias(new ResourceLocation(Constants.MOD_ID, "iron_bullet"), new ResourceLocation(Constants.MOD_ID, "cartridge"));
        ForgeRegistries.ITEMS.addAlias(new ResourceLocation(Constants.MOD_ID, "copper_bullet"), new ResourceLocation(Constants.MOD_ID, "cartridge"));
        ForgeRegistries.ITEMS.addAlias(new ResourceLocation(Constants.MOD_ID, "gold_bullet"), new ResourceLocation(Constants.MOD_ID, "enchanted_cartridge"));
        ForgeRegistries.ITEMS.addAlias(new ResourceLocation(Constants.MOD_ID, "netherite_bullet"), new ResourceLocation(Constants.MOD_ID, "hellfire_cartridge"));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addCreative);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        if (CONSECRATION) {
            bus.addListener(this::enqueueIMC);
        }
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.MUSKET.get());
            event.accept(ModRegistry.CARTRIDGE.get());
            event.accept(ModRegistry.HELLFIRE_CARTRIDGE.get());

            if (CONSECRATION) {
                event.accept(ModRegistry.ENCHANTED_CARTRIDGE.get());
            }
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(MUSKET_PILLAGER_EGG);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Network::register);
        Raid.RaiderType.create(MUSKET_PILLAGER.get().toString(), MUSKET_PILLAGER.get(), new int[]{0, 2, 2, 2, 3, 3, 3, 4, 4});
        ModRegistry.registerSensorGoal();
    }

    public void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                (livingEntity, damageSource) -> (damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.isHoly()));
    }
}