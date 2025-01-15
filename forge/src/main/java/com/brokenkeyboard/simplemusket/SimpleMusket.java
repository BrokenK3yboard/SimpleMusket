package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);

    public SimpleMusket() {
        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        register(bus, Registries.ITEM, ModRegistry::registerItems);
        register(bus, Registries.ENTITY_TYPE, ModRegistry::registerEntity);
        register(bus, Registries.ENCHANTMENT, ModRegistry::registerEnchants);
        register(bus, Registries.MOB_EFFECT, ModRegistry::registerEffects);
        register(bus, Registries.SOUND_EVENT, ModRegistry::registerSounds);
        RECIPE_SERIALIZERS.register(bus);
        GLM.register(bus);

        bus.addListener(this::addCreative);

        if (Services.PLATFORM.isModLoaded("consecration")) {
            bus.addListener(this::enqueueIMC);
        }
    }

    public static <T> void register(IEventBus bus, ResourceKey<Registry<T>> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
        bus.addListener((RegisterEvent event) -> source.accept(((location, t) -> event.register(registry, location, () -> t))));
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.MUSKET);
            event.accept(ModRegistry.CARTRIDGE);
            event.accept(ModRegistry.ENCHANTED_CARTRIDGE);
            event.accept(ModRegistry.HELLFIRE_CARTRIDGE);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModRegistry.GUNSLINGER_EGG);
        }
    }

    public void enqueueIMC(InterModEnqueueEvent event) {
        if (Services.PLATFORM.isModLoaded("consecration")) {
            InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                    (livingEntity, damageSource) -> (damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.getBullet().equals(ModRegistry.ENCHANTED_CARTRIDGE)));
        }

        if (Services.PLATFORM.isModLoaded("usefulspyglass")) {
            InterModComms.sendTo("usefulspyglass", "precision", () -> ModRegistry.AIMING_MUSKET);
        }
    }
}