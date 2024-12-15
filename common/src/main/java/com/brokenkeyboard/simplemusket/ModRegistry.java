package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.enchantment.AmmoCountEffect;
import com.brokenkeyboard.simplemusket.enchantment.ComponentKillEffect;
import com.brokenkeyboard.simplemusket.enchantment.DamageDistanceEffect;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.VillagerHostilesSensorAccessor;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class ModRegistry {

    public static final String MOD_ID = "simplemusket";
    public static final String MOD_NAME = "Simple Musket";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Map<ResourceLocation, EntityType<?>> ENTITIES = new HashMap<>();
    public static final Map<ResourceLocation, Item> ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, SoundEvent> SOUNDS = new HashMap<>();

    public static final EntityType.Builder<BulletEntity> BULLET_BUILDER = EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20);
    static {
        Services.PLATFORM.disableVelocityUpdate(BULLET_BUILDER);
    }

    public static final EntityType<? extends BulletEntity> BULLET_ENTITY = (EntityType<BulletEntity>) addEntity(location("bullet_entity"), BULLET_BUILDER.build("bullet_entity"));
    public static final EntityType<? extends MusketPillager> GUNSLINGER = (EntityType<MusketPillager>) addEntity(location("musket_pillager"),
            EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).canSpawnFarFromPlayer().clientTrackingRange(8).build("musket_pillager"));

    public static final Item MUSKET = addItem(location("musket"), new MusketItem(new Item.Properties().durability(256)));
    public static final Item CARTRIDGE = addItem(location("cartridge"), new BulletItem(6F));
    public static final Item HELLFIRE_CARTRIDGE = addItem(location("hellfire_cartridge"), new BulletItem(6F));
    public static final Item ENCHANTED_CARTRIDGE = addItem(location("enchanted_cartridge"), new BulletItem(8F));
    public static final Item GUNSLINGER_EGG = addItem(location("musket_pillager_spawn_egg"), new SpawnEggItem(GUNSLINGER, 9804699, 5258034, new Item.Properties()));

    public static final TagKey<Item> MUSKET_ENCHANTABLE = TagKey.create(Registries.ITEM, location("enchantable/musket"));
    public static final TagKey<Enchantment> MUSKET_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, location("exclusive_set/musket"));
    public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, location("bullet"));
    public static final ResourceKey<Enchantment> FIREPOWER = ResourceKey.create(Registries.ENCHANTMENT, location("firepower"));
    public static final ResourceKey<Enchantment> LONGSHOT = ResourceKey.create(Registries.ENCHANTMENT, location("longshot"));
    public static final ResourceKey<Enchantment> REPEATING = ResourceKey.create(Registries.ENCHANTMENT, location("repeating"));
    public static final ResourceKey<EnchantmentProvider> GUNSLINGER_SPAWN_MUSKET = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("raid/gunslinger_spawn_musket"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_3 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("raid/gunslinger_post_wave_3"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_5 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, location("raid/gunslinger_post_wave_5"));

    static {
        Services.PLATFORM.createEntityEffectComponent("component_kill", ComponentKillEffect.CODEC);
    }

    public static final DataComponentType<List<ConditionalEffect<DamageDistanceEffect>>> DAMAGE_DISTANCE = Services.PLATFORM.createEnchantmentComponent("damage_distance", builder ->
            builder.persistent(ConditionalEffect.codec(DamageDistanceEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<AmmoCountEffect>>> AMMO_COUNT = Services.PLATFORM.createEnchantmentComponent("ammo_count", builder ->
            builder.persistent(ConditionalEffect.codec(AmmoCountEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<Integer> AMMO_BONUS = Services.PLATFORM.createDataComponent("ammo_bonus", builder -> builder.persistent(Codec.INT));

    public static final Holder<MobEffect> ARMOR_DECREASE_EFFECT = Services.PLATFORM.createEffectHolder("armor_decrease", new ModEffect(MobEffectCategory.HARMFUL, 4595487)
            .addAttributeModifier(Attributes.ARMOR, location("effect.armor_decrease"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final Holder<MobEffect> HEX_EFFECT = Services.PLATFORM.createEffectHolder("hex", new ModEffect(MobEffectCategory.HARMFUL, 15724742));

    public static final SoundEvent MUSKET_LOAD_0 = addSound(location("musket_load0"), SoundEvent.createVariableRangeEvent(location("musket_load0")));
    public static final SoundEvent MUSKET_LOAD_1 = addSound(location("musket_load1"), SoundEvent.createVariableRangeEvent(location("musket_load1")));
    public static final SoundEvent MUSKET_READY = addSound(location("musket_ready"), SoundEvent.createVariableRangeEvent(location("musket_ready")));
    public static final SoundEvent MUSKET_FIRE = addSound(location("musket_fire"), SoundEvent.createVariableRangeEvent(location("musket_fire")));

    public static final ResourceLocation BASTION_OTHER = ResourceLocation.withDefaultNamespace("chests/bastion_other");
    public static final ResourceLocation BASTION_TREASURE = ResourceLocation.withDefaultNamespace("chests/bastion_treasure");
    public static final ResourceLocation BASTION_BRIDGE = ResourceLocation.withDefaultNamespace("chests/bastion_bridge");
    public static final ResourceLocation PIGLIN_BARTER = ResourceLocation.withDefaultNamespace("gameplay/piglin_bartering");

    public static final Predicate<LivingEntity> AIMING_MUSKET = (entity) -> MusketItem.isLoaded(entity.getUseItem());

    public static EntityType<?> addEntity(ResourceLocation location, EntityType<?> type) {
        ENTITIES.put(location, type);
        return type;
    }

    public static Item addItem(ResourceLocation location, Item item) {
        ITEMS.put(location, item);
        return item;
    }

    public static SoundEvent addSound(ResourceLocation location, SoundEvent sound) {
        SOUNDS.put(location, sound);
        return sound;
    }

    public static void registerEntity(BiConsumer<ResourceLocation, EntityType<?>> consumer) {
        for (Map.Entry<ResourceLocation, EntityType<?>> entry : ENTITIES.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public static void registerItems(BiConsumer<ResourceLocation, Item> consumer) {
        for (Map.Entry<ResourceLocation, Item> entry : ITEMS.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public static void registerSounds(BiConsumer<ResourceLocation, SoundEvent> consumer) {
        for (Map.Entry<ResourceLocation, SoundEvent> entry : SOUNDS.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public static ResourceLocation location(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void createEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> consumer) {
        consumer.accept(GUNSLINGER, MusketPillager.createAttributes());
    }

    public static void registerItemProperties() {
        ItemProperties.register(ModRegistry.MUSKET, location("loading"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && !MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, location("load_stage"),
                (stack, world, living, id) -> (living == null || MusketItem.isLoaded(stack)) ? 0.0F :
                        (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / Config.RELOAD_TIME.get());

        ItemProperties.register(ModRegistry.MUSKET, location("loaded"),
                (stack, world, living, id) -> living != null && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, location("aiming"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, location("sawnoff"),
                (stack, world, living, id) -> living instanceof MusketPillager pillager && pillager.isUsingSawnOff() ? 1.0F : 0.0F);
    }

    public static void registerSensorGoal() {
        Map<EntityType<?>, Float> map = new IdentityHashMap<>(VillagerHostilesSensorAccessor.getAcceptableDistance());
        map.put(ModRegistry.GUNSLINGER, 32.0F);
        VillagerHostilesSensorAccessor.setAcceptableDistance(ImmutableMap.copyOf(map));
    }

    public static class ModEffect extends MobEffect {

        public ModEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}