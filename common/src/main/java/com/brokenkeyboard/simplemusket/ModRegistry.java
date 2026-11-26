package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.enchantment.AmmoBonusEffect;
import com.brokenkeyboard.simplemusket.enchantment.AmmoCountEffect;
import com.brokenkeyboard.simplemusket.enchantment.DamageDistanceEffect;
import com.brokenkeyboard.simplemusket.enchantment.FullyAimedPredicate;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.Gunslinger;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.VillagerHostilesSensorAccessor;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ModRegistry {

    public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, location("bullet"));
    public static final ResourceKey<EnchantmentProvider> GUNSLINGER_SPAWN_MUSKET = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("gunslinger_spawn_musket"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_3 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("raid/gunslinger_post_wave_3"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_5 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("raid/gunslinger_post_wave_5"));

    public static final Holder<EntityType<Gunslinger>> GUNSLINGER = Services.PLATFORM.createEntity("gunslinger", () -> Gunslinger::new, MobCategory.MONSTER, builder -> builder
            .sized(0.6F, 1.95F)
            .canSpawnFarFromPlayer()
            .clientTrackingRange(8)
            .notInPeaceful());

    public static final Holder<EntityType<BulletEntity>> BULLET_ENTITY = Services.PLATFORM.createEntity("bullet", () -> BulletEntity::new, MobCategory.MISC, builder -> builder
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20));

    public static final Holder<Item> MUSKET = Services.PLATFORM.createItem("musket", () -> MusketItem::new, properties -> properties.durability(384).enchantable(1));
    public static final Holder<Item> CARTRIDGE = Services.PLATFORM.createItem("cartridge", () -> BulletItem::new, properties -> properties);
    public static final Holder<Item> GUNSLINGER_EGG = Services.PLATFORM.createItem("gunslinger_spawn_egg", () -> SpawnEggItem::new, properties -> properties.spawnEgg(GUNSLINGER.value()));

    public static final Holder<SoundEvent> MUSKET_LOAD_0 = Services.PLATFORM.createSound("musket_load0", () -> SoundEvent.createVariableRangeEvent(location("musket_load0")));
    public static final Holder<SoundEvent> MUSKET_LOAD_1 = Services.PLATFORM.createSound("musket_load1", () -> SoundEvent.createVariableRangeEvent(location("musket_load1")));
    public static final Holder<SoundEvent> MUSKET_READY = Services.PLATFORM.createSound("musket_ready", () -> SoundEvent.createVariableRangeEvent(location("musket_ready")));
    public static final Holder<SoundEvent> MUSKET_FIRE = Services.PLATFORM.createSound("musket_fire", () -> SoundEvent.createVariableRangeEvent(location("musket_fire")));

    public static final Holder<DataComponentType<Integer>> AMMO_BONUS = Services.PLATFORM.createDataComponent("ammo_bonus", builder -> builder.persistent(Codec.INT));

    static {
        Services.PLATFORM.createEntitySubPredicate("fully_aimed", FullyAimedPredicate.CODEC);
        Services.PLATFORM.createEntityEffectComponent("ammo_bonus", AmmoBonusEffect.CODEC);
    }

    public static final Holder<DataComponentType<List<ConditionalEffect<DamageDistanceEffect>>>> DAMAGE_DISTANCE = Services.PLATFORM.createEnchantmentComponent("damage_distance", builder ->
            builder.persistent(ConditionalEffect.codec(DamageDistanceEffect.CODEC).listOf()));
    public static final Holder<DataComponentType<List<ConditionalEffect<AmmoCountEffect>>>> AMMO_COUNT = Services.PLATFORM.createEnchantmentComponent("ammo_count", builder ->
            builder.persistent(ConditionalEffect.codec(AmmoCountEffect.CODEC).listOf()));

    public static Identifier location(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

    public static void createEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> consumer) {
        consumer.accept(GUNSLINGER.value(), Gunslinger.createAttributes());
    }

    public static void registerSensorGoal() {
        Map<EntityType<?>, Float> map = new IdentityHashMap<>(VillagerHostilesSensorAccessor.getAcceptableDistance());
        map.put(GUNSLINGER.value(), 32.0F);
        VillagerHostilesSensorAccessor.setAcceptableDistance(ImmutableMap.copyOf(map));
    }

    public static final Predicate<LivingEntity> AIMING_MUSKET = (entity) -> MusketItem.isLoaded(entity.getUseItem())
            && entity.getUseItem().getUseDuration(entity) - entity.getUseItemRemainingTicks() > Config.AIM_TIME.get();

    @SuppressWarnings("EmptyMethod")
    public static void bootstrap() {}
}