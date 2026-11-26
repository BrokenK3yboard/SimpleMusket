package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.SMSkeletonLootModifier;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class SimpleMusket {

    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Constants.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Constants.MOD_ID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Constants.MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENCHANTMENT_ENTITY_COMPONENTS = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Constants.MOD_ID);
    public static final DeferredRegister.DataComponents ENCHANTMENT_COMPONENTS = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> DATA_ATTACHNMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Constants.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SMSkeletonLootModifier>> SKELETON_LOOT = GLM.register("skeleton_loot", SMSkeletonLootModifier.CODEC);
    public static final Supplier<AttachmentType<Boolean>> USING_BAYONET = DATA_ATTACHNMENTS.register(Constants.USING_BAYONET, () -> AttachmentType.builder(() -> false)
            .serialize(Codec.BOOL.fieldOf(Constants.USING_BAYONET))
            .sync(ByteBufCodecs.BOOL).build());

    public SimpleMusket(ModContainer container, IEventBus bus) {
        ModRegistry.bootstrap();
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ENTITIES.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        EFFECTS.register(bus);
        DATA_COMPONENTS.register(bus);
        ENTITY_SUB_PREDICATES.register(bus);
        ENCHANTMENT_COMPONENTS.register(bus);
        ENCHANTMENT_ENTITY_COMPONENTS.register(bus);
        DATA_ATTACHNMENTS.register(bus);
        GLM.register(bus);
        bus.addListener(this::addCreative);
        bus.addListener(this::sendIMC);
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.MUSKET.value());
            event.accept(ModRegistry.CARTRIDGE.value());
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModRegistry.GUNSLINGER_EGG.value());
        }
    }

    public void sendIMC(InterModEnqueueEvent event) {
        if (Services.PLATFORM.isModLoaded(Constants.USEFUL_SPYGLASS_MOD_ID)) {
            InterModComms.sendTo(Constants.USEFUL_SPYGLASS_MOD_ID, Constants.USEFUL_SPYGLASS_PRECISION_ENCHANTMENT_ID, () -> ModRegistry.AIMING_MUSKET);
        }
    }

    public static Object getRaiderEntity(int idx, Class<?> type) {
        return type.cast(switch (idx) {
            case 0 -> (ModRegistry.GUNSLINGER.value());
            case 1 -> Constants.GUNSLINGER_RAID_SPAWN_COUNTS;
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        });
    }
}