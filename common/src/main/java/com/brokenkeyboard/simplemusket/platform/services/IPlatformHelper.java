package com.brokenkeyboard.simplemusket.platform.services;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
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

public interface IPlatformHelper {

    boolean isModLoaded(String modId);
    void playSound(SoundSource source, ServerLevel level, Vec3 origin);
    <T extends Entity> Holder<EntityType<T>> createEntity(String name, Supplier<EntityType.EntityFactory<T>> entityFactory, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> operator);
    Holder<Item> createItem(String name, Supplier<Function<Item.Properties, Item>> itemFactory, UnaryOperator<Item.Properties> properties);
    Holder<SoundEvent> createSound(String name,  Supplier<SoundEvent> soundEvent);
    <T> Holder<DataComponentType<T>> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec);
    <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec);
    <T> Holder<DataComponentType<T>> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    void setMobUsingBayonet(Mob mob, boolean isUsingBayonet);
    boolean isMobUsingBayonet(Mob mob);
}