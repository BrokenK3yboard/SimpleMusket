package com.brokenkeyboard.simplemusket.platform.services;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.function.UnaryOperator;

public interface IPlatformHelper {

    boolean isModLoaded(String modId);
    void playSound(SoundSource source, ServerLevel level, Vec3 origin);
    Entity getHitEntity(Entity entity);
    Holder<MobEffect> createEffectHolder(String name, MobEffect effect);
    <T> DataComponentType<T> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    <T> DataComponentType<T> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator);
    <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec);
    <T extends CraftingRecipe> RecipeSerializer<T> createRecipeSerializer(String name, RecipeSerializer<T> serializer);
    void disableVelocityUpdate(EntityType.Builder<?> builder);
}