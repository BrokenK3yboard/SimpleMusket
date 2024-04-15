package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.network.Network;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTabs;
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
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.brokenkeyboard.simplemusket.ModRegistry.MUSKET_PILLAGER;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);

    public SimpleMusket() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        register(Registries.ITEM, ModRegistry::registerItems);
        register(Registries.ENTITY_TYPE, ModRegistry::registerEntity);
        register(Registries.ENCHANTMENT, ModRegistry::registerEnchants);
        register(Registries.MOB_EFFECT, ModRegistry::registerEffects);
        register(Registries.SOUND_EVENT, ModRegistry::registerSounds);
        GLM.register(bus);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addCreative);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        if (Services.PLATFORM.isModLoaded("consecration")) {
            bus.addListener(this::enqueueIMC);
        }
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent event) ->
                source.accept(((location, t) -> event.register(registry, location, () -> t))));
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.MUSKET);
            event.accept(ModRegistry.CARTRIDGE);
            event.accept(ModRegistry.HELLFIRE_CARTRIDGE);

            if (Services.PLATFORM.isModLoaded("consecration")) {
                event.accept(ModRegistry.ENCHANTED_CARTRIDGE);
            }
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModRegistry.MUSKET_PILLAGER_EGG);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Network::register);
        Raid.RaiderType.create(MUSKET_PILLAGER.toString(), MUSKET_PILLAGER, new int[]{0, 2, 2, 2, 3, 3, 3, 4, 4});
        ModRegistry.registerSensorGoal();
    }

    public void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                (livingEntity, damageSource) -> (damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.isHoly()));
    }
}