package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod(ModRegistry.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModRegistry.MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModRegistry.MOD_ID);
    public static final DeferredRegister.DataComponents ENCHANTMENT_COMPONENTS = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ModRegistry.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENCHANTMENT_ENTITY_COMPONENTS = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, ModRegistry.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModRegistry.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BastionLoot>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<PiglinBarter>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);

    public SimpleMusket(ModContainer container, IEventBus bus) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        register(Registries.ITEM, ModRegistry::registerItems, bus);
        register(Registries.ENTITY_TYPE, ModRegistry::registerEntity, bus);
        register(Registries.SOUND_EVENT, ModRegistry::registerSounds, bus);
        EFFECTS.register(bus);
        DATA_COMPONENTS.register(bus);
        ENCHANTMENT_COMPONENTS.register(bus);
        ENCHANTMENT_ENTITY_COMPONENTS.register(bus);
        bus.addListener((EntityAttributeCreationEvent event) -> ModRegistry.createEntityAttributes((type, builder) -> event.put(type, builder.build())));
        GLM.register(bus);
        bus.addListener(this::addCreative);
        bus.addListener(this::sendIMC);
    }

    public static <T> void register(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<ResourceLocation, T>> source, IEventBus bus) {
        bus.addListener((RegisterEvent event) -> source.accept(((location, t) -> event.register(registry, location, () -> t))));
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.MUSKET);
            event.accept(ModRegistry.CARTRIDGE);
            event.accept(ModRegistry.HELLFIRE_CARTRIDGE);
            event.accept(ModRegistry.ENCHANTED_CARTRIDGE);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModRegistry.GUNSLINGER_EGG);
        }
    }

    public void sendIMC(InterModEnqueueEvent event) {
        if (Services.PLATFORM.isModLoaded("consecration")) {
            InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                    (livingEntity, damageSource) -> (damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.getBullet().equals(ModRegistry.ENCHANTED_CARTRIDGE)));
        }

        if (Services.PLATFORM.isModLoaded("usefulspyglass")) {
            InterModComms.sendTo("usefulspyglass", "precision", () -> ModRegistry.AIMING_MUSKET);
        }
    }

    public static Object getRaiderEntity(int idx, Class<?> type) {
        return type.cast(switch (idx) {
            case 0 -> (Supplier<EntityType<? extends Raider>>) () -> ModRegistry.GUNSLINGER;
            case 1 -> new int[] {0, 1, 1, 1, 2, 2, 2, 2, 3};
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        });
    }
}