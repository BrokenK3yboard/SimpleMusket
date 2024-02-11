package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.effect.ArmorDecreaseEffect;
import com.brokenkeyboard.simplemusket.enchantment.*;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.VillagerHostilesSensorAccessor;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.brokenkeyboard.simplemusket.registry.RegistrationProvider;
import com.brokenkeyboard.simplemusket.registry.RegistryObject;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.IdentityHashMap;
import java.util.Map;

public class ModRegistry {

    public static final boolean CONSECRATION = Services.PLATFORM.isModLoaded("consecration");

    private static final RegistrationProvider<EntityType<?>> ENTITIES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MOD_ID);
    private static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MOD_ID);
    private static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, Constants.MOD_ID);
    private static final RegistrationProvider<Enchantment> ENCHANTMENTS = RegistrationProvider.get(Registries.ENCHANTMENT, Constants.MOD_ID);
    private static final RegistrationProvider<MobEffect> EFFECTS = RegistrationProvider.get(Registries.MOB_EFFECT, Constants.MOD_ID);

    public static final RegistryObject<EntityType<BulletEntity>> BULLET_ENTITY = ENTITIES.register("bullet_entity", () ->
            EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(5)
            .build("bullet_entity"));

    public static final RegistryObject<EntityType<MusketPillager>> MUSKET_PILLAGER = ENTITIES.register("musket_pillager", () ->
            EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .canSpawnFarFromPlayer()
            .clientTrackingRange(8)
            .build("musket_pillager"));

    public static final RegistryObject<Item> MUSKET = ITEMS.register("musket", () -> new MusketItem(new Item.Properties()));
    public static final RegistryObject<Item> CARTRIDGE = ITEMS.register("cartridge", () -> new BulletItem(16, 0.5));
    public static final RegistryObject<Item> HELLFIRE_CARTRIDGE = ITEMS.register("hellfire_cartridge", () -> new BulletItem(16, 0.5));
    public static final RegistryObject<Item> ENCHANTED_CARTRIDGE = ITEMS.register("enchanted_cartridge", () -> new BulletItem(8, 0.5));

    public static final EnchantmentCategory FIREARM = Services.PLATFORM.musketCategory();

    public static final RegistryObject<Enchantment> FIREPOWER = ENCHANTMENTS.register("firepower", () ->
            new FirepowerEnchantment(Enchantment.Rarity.COMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DEADEYE = ENCHANTMENTS.register("deadeye", () ->
            new DeadeyeEnchantment(Enchantment.Rarity.UNCOMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> LONGSHOT = ENCHANTMENTS.register("longshot", () ->
            new LongshotEnchantment(Enchantment.Rarity.RARE, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> BLAST = ENCHANTMENTS.register("blast", () ->
            new BlastEnchantment(Enchantment.Rarity.RARE, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> REPEATING = ENCHANTMENTS.register("repeating", () ->
            new RepeatingEnchantment(Enchantment.Rarity.VERY_RARE, FIREARM, EquipmentSlot.MAINHAND));

    public static final RegistryObject<MobEffect> ARMOR_DECREASE = EFFECTS.register("armor_decrease", () ->
            new ArmorDecreaseEffect(MobEffectCategory.HARMFUL, 552727).addAttributeModifier(Attributes.ARMOR, "E8BA6690-8212-421F-8FCF-69A88DEBA4F8", -0.25, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<SoundEvent> MUSKET_LOAD_0 = SOUNDS.register("musket_load0", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_load0")));
    public static final RegistryObject<SoundEvent> MUSKET_LOAD_1 = SOUNDS.register("musket_load1", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_load1")));
    public static final RegistryObject<SoundEvent> MUSKET_READY = SOUNDS.register("musket_ready", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_ready")));
    public static final RegistryObject<SoundEvent> MUSKET_FIRE = SOUNDS.register("musket_fire", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "musket_fire")));

    public static void registerItemProperties() {

        ItemProperties.register(ModRegistry.MUSKET.get(), new ResourceLocation(Constants.MOD_ID, "loading"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && !MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET.get(), new ResourceLocation(Constants.MOD_ID, "load_stage"),
                (stack, world, living, id) -> (living == null || MusketItem.hasAmmo(stack)) ? 0.0F :
                        MusketItem.getReloadPerc(stack, stack.getUseDuration() - living.getUseItemRemainingTicks()));

        ItemProperties.register(ModRegistry.MUSKET.get(), new ResourceLocation(Constants.MOD_ID, "loaded"),
                (stack, world, living, id) -> living != null && MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistry.MUSKET.get(), new ResourceLocation(Constants.MOD_ID, "aiming"),
                (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                        && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);
    }

    public static void registerSensorGoal() {
        Map<EntityType<?>, Float> map = new IdentityHashMap<>(VillagerHostilesSensorAccessor.getAcceptableDistance());
        map.put(ModRegistry.MUSKET_PILLAGER.get(), 24.0F);
        VillagerHostilesSensorAccessor.setAcceptableDistance(ImmutableMap.copyOf(map));
    }

    @SuppressWarnings("EmptyMethod")
    public static void bootstrap() {}
}