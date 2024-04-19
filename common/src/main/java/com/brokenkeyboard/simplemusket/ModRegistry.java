package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.effect.ArmorDecreaseEffect;
import com.brokenkeyboard.simplemusket.enchantment.*;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.VillagerHostilesSensorAccessor;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class ModRegistry {

    public static final Map<ResourceLocation, EntityType<?>> ENTITIES = new HashMap<>();
    public static final Map<ResourceLocation, Item> ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, Enchantment> ENCHANTMENTS = new HashMap<>();
    public static final Map<ResourceLocation, MobEffect> EFFECTS = new HashMap<>();
    public static final Map<ResourceLocation, SoundEvent> SOUNDS = new HashMap<>();

    public static final EntityType<? extends BulletEntity> BULLET_ENTITY = (EntityType<BulletEntity>) addEntity(new ResourceLocation(Constants.MOD_ID, "bullet_entity"),
            EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(5).build("bullet_entity"));
    public static final EntityType<? extends MusketPillager> MUSKET_PILLAGER = (EntityType<MusketPillager>) addEntity(new ResourceLocation(Constants.MOD_ID, "musket_pillager"),
            EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).canSpawnFarFromPlayer().clientTrackingRange(8).build("musket_pillager"));

    public static final Item MUSKET = addItem(new ResourceLocation(Constants.MOD_ID, "musket"), new MusketItem(new Item.Properties()));
    public static final Item CARTRIDGE = addItem(new ResourceLocation(Constants.MOD_ID, "cartridge"), new BulletItem(16, 0.5));
    public static final Item HELLFIRE_CARTRIDGE = addItem(new ResourceLocation(Constants.MOD_ID, "hellfire_cartridge"), new BulletItem(16, 0.5));
    public static final Item ENCHANTED_CARTRIDGE = addItem(new ResourceLocation(Constants.MOD_ID, "enchanted_cartridge"), new BulletItem(8, 0.5));
    public static final Item MUSKET_PILLAGER_EGG = addItem(new ResourceLocation(Constants.MOD_ID, "musket_pillager_spawn_egg"), new SpawnEggItem(MUSKET_PILLAGER, 5258034, 2960169, new Item.Properties()));

    public static final EnchantmentCategory FIREARM = Services.PLATFORM.musketCategory();
    public static final Enchantment FIREPOWER = addEnchant(new ResourceLocation(Constants.MOD_ID, "firepower"), new FirepowerEnchantment(FIREARM, EquipmentSlot.MAINHAND));
    public static final Enchantment DEADEYE = addEnchant(new ResourceLocation(Constants.MOD_ID, "deadeye"), new DeadeyeEnchantment(FIREARM, EquipmentSlot.MAINHAND));
    public static final Enchantment LONGSHOT = addEnchant(new ResourceLocation(Constants.MOD_ID, "longshot"), new LongshotEnchantment(FIREARM, EquipmentSlot.MAINHAND));
    public static final Enchantment BLAST = addEnchant(new ResourceLocation(Constants.MOD_ID, "blast"), new BlastEnchantment(FIREARM, EquipmentSlot.MAINHAND));
    public static final Enchantment REPEATING = addEnchant(new ResourceLocation(Constants.MOD_ID, "repeating"), new RepeatingEnchantment(FIREARM, EquipmentSlot.MAINHAND));

    public static final MobEffect ARMOR_DECREASE = addEffect(new ResourceLocation(Constants.MOD_ID, "armor_decrease"), new ArmorDecreaseEffect(MobEffectCategory.HARMFUL, 552727)
            .addAttributeModifier(Attributes.ARMOR, "E8BA6690-8212-421F-8FCF-69A88DEBA4F8", -0.25, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final SoundEvent MUSKET_LOAD_0 = addSound(new ResourceLocation(Constants.MOD_ID, "musket_load0"), SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_load0")));
    public static final SoundEvent MUSKET_LOAD_1 = addSound(new ResourceLocation(Constants.MOD_ID, "musket_load1"), SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_load1")));
    public static final SoundEvent MUSKET_READY = addSound(new ResourceLocation(Constants.MOD_ID, "musket_ready"), SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_ready")));
    public static final SoundEvent MUSKET_FIRE = addSound(new ResourceLocation(Constants.MOD_ID, "musket_fire"), SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_fire")));

    public static EntityType<?> addEntity(ResourceLocation location, EntityType<?> type) {
        ENTITIES.put(location, type);
        return type;
    }

    public static Item addItem(ResourceLocation location, Item item) {
        ITEMS.put(location, item);
        return item;
    }

    public static Enchantment addEnchant(ResourceLocation location, Enchantment enchantment) {
        ENCHANTMENTS.put(location, enchantment);
        return enchantment;
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

    public static void registerEnchants(BiConsumer<ResourceLocation, Enchantment> consumer) {
        for (Map.Entry<ResourceLocation, Enchantment> entry : ENCHANTMENTS.entrySet()) {
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

    public static void registerItemProperties() {

        ItemProperties.register(ModRegistry.MUSKET, new ResourceLocation(Constants.MOD_ID, "loading"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && !MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, new ResourceLocation(Constants.MOD_ID, "load_stage"),
                (stack, world, living, id) -> (living == null || MusketItem.hasAmmo(stack)) ? 0.0F :
                        MusketItem.getReloadPerc(stack, stack.getUseDuration() - living.getUseItemRemainingTicks()));

        ItemProperties.register(ModRegistry.MUSKET, new ResourceLocation(Constants.MOD_ID, "loaded"),
                (stack, world, living, id) -> living != null && MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, new ResourceLocation(Constants.MOD_ID, "aiming"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET, new ResourceLocation(Constants.MOD_ID, "sawnoff"),
                (stack, world, living, id) -> living instanceof MusketPillager pillager && pillager.isUsingSawnOff() ? 1.0F : 0.0F);
    }

    public static void registerSensorGoal() {
        Map<EntityType<?>, Float> map = new IdentityHashMap<>(VillagerHostilesSensorAccessor.getAcceptableDistance());
        map.put(ModRegistry.MUSKET_PILLAGER, 24.0F);
        VillagerHostilesSensorAccessor.setAcceptableDistance(ImmutableMap.copyOf(map));
    }
}