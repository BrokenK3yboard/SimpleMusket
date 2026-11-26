package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
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
    public <T extends Entity> Holder<EntityType<T>> createEntity(String name, Supplier<EntityType.EntityFactory<T>> entityFactory, MobCategory mobCategory, UnaryOperator<EntityType.Builder<T>> operator) {
        return ((Holder<EntityType<T>>) (Holder<?>) SimpleMusket.ENTITIES.registerEntityType(name, entityFactory.get(), mobCategory, operator));
    }

    @Override
    public Holder<Item> createItem(String name, Supplier<Function<Item.Properties, Item>> itemFactory, UnaryOperator<Item.Properties> properties) {
        return SimpleMusket.ITEMS.registerItem(name, itemFactory.get(), () -> properties.apply(new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, ModRegistry.location(name))));
    }

    @Override
    public Holder<SoundEvent> createSound(String name, Supplier<SoundEvent> soundEvent) {
        return SimpleMusket.SOUNDS.register(name, soundEvent);
    }

    @Override
    public <T> Holder<DataComponentType<T>> createDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return ((Holder<DataComponentType<T>>) (Holder<?>) SimpleMusket.DATA_COMPONENTS.registerComponentType(name, operator));
    }

    @Override
    public <T extends EntitySubPredicate> void createEntitySubPredicate(String name, MapCodec<T> codec) {
        SimpleMusket.ENTITY_SUB_PREDICATES.register(name, () -> codec);
    }

    @Override
    public <T extends EnchantmentEntityEffect> void createEntityEffectComponent(String name, MapCodec<T> codec) {
        SimpleMusket.ENCHANTMENT_ENTITY_COMPONENTS.register(name, () -> codec);
    }

    @Override
    public <T> Holder<DataComponentType<T>> createEnchantmentComponent(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return ((Holder<DataComponentType<T>>) (Holder<?>) SimpleMusket.ENCHANTMENT_COMPONENTS.register(name, () -> operator.apply(DataComponentType.builder()).build()));
    }

    @Override
    public void setMobUsingBayonet(Mob mob, boolean isUsingBayonet) {
        mob.setData(SimpleMusket.USING_BAYONET, isUsingBayonet);
    }

    @Override
    public boolean isMobUsingBayonet(Mob mob) {
        return mob.getData(SimpleMusket.USING_BAYONET);
    }
}