package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.effect.ModEffect;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.VillagerHostilesSensorAccessor;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class ModRegistry {

    public static final Map<ResourceLocation, EntityType<?>> ENTITIES = new HashMap<>();
    public static final Map<ResourceLocation, Item> ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, MobEffect> EFFECTS = new HashMap<>();
    public static final Map<ResourceLocation, SoundEvent> SOUNDS = new HashMap<>();

    public static final EntityType<? extends BulletEntity> BULLET_ENTITY = (EntityType<BulletEntity>) addEntity(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bullet_entity"),
            EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(5).build("bullet_entity"));
    public static final EntityType<? extends MusketPillager> MUSKET_PILLAGER = (EntityType<MusketPillager>) addEntity(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_pillager"),
            EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).canSpawnFarFromPlayer().clientTrackingRange(8).build("musket_pillager"));

    public static final Item MUSKET = addItem(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket"), new MusketItem(new Item.Properties().durability(256)));
    public static final Item CARTRIDGE = addItem(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "cartridge"), new BulletItem());
    public static final Item HELLFIRE_CARTRIDGE = addItem(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "hellfire_cartridge"), new BulletItem());
    public static final Item ENCHANTED_CARTRIDGE = addItem(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "enchanted_cartridge"), new BulletItem());
    public static final Item MUSKET_PILLAGER_EGG = addItem(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_pillager_spawn_egg"), new SpawnEggItem(MUSKET_PILLAGER, 5258034, 2960169, new Item.Properties()));

    public static final TagKey<Item> MUSKET_ENCHANTABLE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "enchantable/musket"));
    public static final TagKey<Enchantment> MUSKET_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "exclusive_set/musket"));
    public static final ResourceKey<DamageType> BULLET = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bullet"));
    public static final ResourceKey<Enchantment> FIREPOWER = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "firepower"));
    public static final ResourceKey<Enchantment> DEADEYE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "deadeye"));
    public static final ResourceKey<Enchantment> LONGSHOT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "longshot"));
    public static final ResourceKey<Enchantment> REPEATING = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "repeating"));
    public static final ResourceKey<EnchantmentProvider> GUNSLINGER_SPAWN_MUSKET = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "raid/gunslinger_spawn_musket"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_3 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "raid/gunslinger_post_wave_3"));
    public static final ResourceKey<EnchantmentProvider> RAID_GUNSLINGER_POST_WAVE_5 = ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "raid/gunslinger_post_wave_5"));

    public static final MobEffect ARMOR_DECREASE = addEffect(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "armor_decrease"), new ModEffect(MobEffectCategory.HARMFUL, 4595487)
            .addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "effect.armor_decrease"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final MobEffect HEX = addEffect(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "hex"), new ModEffect(MobEffectCategory.HARMFUL, 15724742));

    public static final Holder<MobEffect> ARMOR_DECREASE_EFFECT = Services.PLATFORM.createArmorDecreaseEffect();
    public static final Holder<MobEffect> HEX_EFFECT = Services.PLATFORM.createHexEffect();

    public static final SoundEvent MUSKET_LOAD_0 = addSound(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_load0"), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_load0")));
    public static final SoundEvent MUSKET_LOAD_1 = addSound(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_load1"), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_load1")));
    public static final SoundEvent MUSKET_READY = addSound(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_ready"), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_ready")));
    public static final SoundEvent MUSKET_FIRE = addSound(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_fire"), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_fire")));

    public static EntityType<?> addEntity(ResourceLocation location, EntityType<?> type) {
        ENTITIES.put(location, type);
        return type;
    }

    public static Item addItem(ResourceLocation location, Item item) {
        ITEMS.put(location, item);
        return item;
    }

    public static MobEffect addEffect(ResourceLocation location, MobEffect effect) {
        EFFECTS.put(location, effect);
        return effect;
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

    public static void registerEffects(BiConsumer<ResourceLocation, MobEffect> consumer) {
        for (Map.Entry<ResourceLocation, MobEffect> entry : EFFECTS.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public static void registerSounds(BiConsumer<ResourceLocation, SoundEvent> consumer) {
        for (Map.Entry<ResourceLocation, SoundEvent> entry : SOUNDS.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public static void createEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> consumer) {
        consumer.accept(MUSKET_PILLAGER, MusketPillager.createAttributes());
    }

    public static void registerItemProperties() {

        ItemProperties.register(ModRegistry.MUSKET, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "loading"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && !MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "load_stage"),
                (stack, world, living, id) -> (living == null || MusketItem.isLoaded(stack)) ? 0.0F :
                        (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / Config.RELOAD_TIME.get());

        ItemProperties.register(ModRegistry.MUSKET, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "loaded"),
                (stack, world, living, id) -> living != null && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "aiming"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sawnoff"),
                (stack, world, living, id) -> living instanceof MusketPillager pillager && pillager.isUsingSawnOff() ? 1.0F : 0.0F);
    }

    public static void registerSensorGoal() {
        Map<EntityType<?>, Float> map = new IdentityHashMap<>(VillagerHostilesSensorAccessor.getAcceptableDistance());
        map.put(ModRegistry.MUSKET_PILLAGER, 40F);
        VillagerHostilesSensorAccessor.setAcceptableDistance(ImmutableMap.copyOf(map));
    }
}