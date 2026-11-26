package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public void playSound(SoundSource source, ServerLevel level, Vec3 origin) {
        for (ServerPlayer serverPlayer : PlayerLookup.around(level, origin, 64)) {
            ServerPlayNetworking.send(serverPlayer, new S2CSoundPayload(source.toString(), origin.toVector3f()));
        }
    }

    @Override
    public <T extends Entity> Holder<EntityType<T>> createEntity(String name, Supplier<EntityType.EntityFactory<T>> entityFactory, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> operator) {
        return Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, ModRegistry.location(name), operator.apply(EntityType.Builder.of(entityFactory.get(), mobCategory)).build(ResourceKey.create(Registries.ENTITY_TYPE, ModRegistry.location(name))));
    }

    @Override
    public Holder<Item> createItem(String name, Supplier<Function<Item.Properties, Item>> itemFactory, UnaryOperator<Item.Properties> properties) {
        return Registry.registerForHolder(BuiltInRegistries.ITEM, ModRegistry.location(name), itemFactory.get().apply(properties.apply(new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, ModRegistry.location(name)))));
    }

    @Override
    public Holder<SoundEvent> createSound(String name, Supplier<SoundEvent> soundEvent) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, ModRegistry.location(name), soundEvent.get());
    }

    @Override
    public <T> Holder<DataComponentType<T>> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.registerForHolder(BuiltInRegistries.DATA_COMPONENT_TYPE, ModRegistry.location(name), operator.apply(DataComponentType.builder()).build());
    }

    @Override
    public <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec) {
        Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, ModRegistry.location(name), codec);
    }

    @Override
    public <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec) {
        Registry.registerForHolder(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, ModRegistry.location(name), codec);
    }

    @Override
    public <T> Holder<DataComponentType<T>> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.registerForHolder(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ModRegistry.location(name), operator.apply(DataComponentType.builder()).build());
    }

    @Override
    public void setMobUsingBayonet(Mob mob, boolean isUsingBayonet) {
        mob.setAttached(SimpleMusket.IS_USING_BAYONET, isUsingBayonet);
    }

    @Override
    public boolean isMobUsingBayonet(Mob mob) {
        return mob.getAttachedOrElse(SimpleMusket.IS_USING_BAYONET, false);
    }
}