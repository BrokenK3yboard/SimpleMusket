package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    public static final RegistryOps<Codec<? extends IGlobalLootModifier>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final RegistryOps<Codec<? extends IGlobalLootModifier>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);

    public SimpleMusket(IEventBus bus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        register(Registries.ITEM, ModRegistry::registerItems);
        register(Registries.ENTITY_TYPE, ModRegistry::registerEntity);
        register(Registries.ENCHANTMENT, ModRegistry::registerEnchants);
        register(Registries.MOB_EFFECT, ModRegistry::registerEffects);
        register(Registries.SOUND_EVENT, ModRegistry::registerSounds);
        GLM.register(bus);
        bus.addListener(this::addCreative);

        if (Services.PLATFORM.isModLoaded("consecration")) {
            bus.addListener(this::enqueueIMC);
        }
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
        NeoForge.EVENT_BUS.addListener((RegisterEvent event) ->
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

    public void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                (livingEntity, damageSource) -> (damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.isHoly()));
    }
}