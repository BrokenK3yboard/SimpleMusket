package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.UnaryOperator;

import static com.brokenkeyboard.simplemusket.SimpleMusket.*;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void playSound(SoundSource source, ServerLevel level, Vec3 origin) {
        PacketDistributor.sendToPlayersNear(level, null, origin.x, origin.y, origin.z, 64, new S2CSoundPayload(source.toString(), origin.toVector3f()));
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof PartEntity<?> part ? part.getParent() : entity;
    }

    @Override
    public Holder<MobEffect> createEffectHolder(String name, MobEffect effect) {
        return EFFECTS.register(name, () -> effect);
    }

    @Override
    public <T> DataComponentType<T> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> component = operator.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> component);
        return component;
    }

    @Override
    public <T> DataComponentType<T> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        DataComponentType<T> component = operator.apply(DataComponentType.builder()).build();
        ENCHANTMENT_COMPONENTS.register(name, () -> component);
        return component;
    }

    @Override
    public <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec) {
        ENCHANTMENT_ENTITY_COMPONENTS.register(name, () -> codec);
    }

    @Override
    public <T extends CraftingRecipe> RecipeSerializer<T> createRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        RECIPE_SERIALIZERS.register(name, () -> serializer);
        return serializer;
    }

    @Override
    public void disableVelocityUpdate(EntityType.Builder<?> builder) {
        builder.setShouldReceiveVelocityUpdates(false);
    }
}