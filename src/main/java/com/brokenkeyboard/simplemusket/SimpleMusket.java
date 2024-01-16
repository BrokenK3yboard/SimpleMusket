package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter;
import com.brokenkeyboard.simplemusket.enchantment.DeadeyeEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.FirepowerEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.LongshotEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.RepeatingEnchantment;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.network.Network;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@Mod(SimpleMusket.MOD_ID)
public class SimpleMusket {
    public static final String MOD_ID = "simplemusket";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final EnchantmentCategory FIREARM = EnchantmentCategory.create("FIREARM", item -> item instanceof MusketItem);

    public static final RegistryObject<EntityType<BulletEntity>> BULLET_ENTITY = ENTITIES.register("bullet_entity", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(5)
            .build("bullet_entity"));

    public static final RegistryObject<EntityType<MusketPillager>> MUSKET_PILLAGER = ENTITIES.register("musket_pillager", () -> EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .canSpawnFarFromPlayer()
            .clientTrackingRange(8)
            .build("musket_pillager"));

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BASTION_LOOT = GLM.register("bastion_loot", BastionLoot.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarter.CODEC);
    public static final RegistryObject<MusketItem> MUSKET = ITEMS.register("musket", () -> new MusketItem(new net.minecraft.world.item.Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_BULLET = ITEMS.register("iron_bullet", () -> new BulletItem(16, 0.5, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> COPPER_BULLET = ITEMS.register("copper_bullet", () -> new BulletItem(4, 0, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLD_BULLET = ITEMS.register("gold_bullet", () -> new BulletItem(9, 0, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_BULLET = ITEMS.register("netherite_bullet", () -> new BulletItem(20, 1, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> MUSKET_PILLAGER_EGG = ITEMS.register("musket_pillager_spawn_egg", () -> new ForgeSpawnEggItem(MUSKET_PILLAGER, 5258034, 2960169, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Enchantment> FIREPOWER = ENCHANTMENTS.register("firepower", () -> new FirepowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DEADEYE = ENCHANTMENTS.register("deadeye", () -> new DeadeyeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> LONGSHOT = ENCHANTMENTS.register("longshot", () -> new LongshotEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> REPEATING = ENCHANTMENTS.register("repeating", () -> new RepeatingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));

    public static final RegistryObject<SoundEvent> MUSKET_LOAD_0 = SOUNDS.register("musket_load0", () -> new SoundEvent(new ResourceLocation(MOD_ID, "musket_load0")));
    public static final RegistryObject<SoundEvent> MUSKET_LOAD_1 = SOUNDS.register("musket_load1", () -> new SoundEvent(new ResourceLocation(MOD_ID, "musket_load1")));
    public static final RegistryObject<SoundEvent> MUSKET_READY = SOUNDS.register("musket_ready", () -> new SoundEvent(new ResourceLocation(MOD_ID, "musket_ready")));
    public static final RegistryObject<SoundEvent> MUSKET_FIRE = SOUNDS.register("musket_fire", () -> new SoundEvent(new ResourceLocation(MOD_ID, "musket_fire")));

    public static final boolean CONSECRATION = ModList.get().isLoaded("consecration");

    public SimpleMusket() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.registerConfig();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        ENCHANTMENTS.register(bus);
        SOUNDS.register(bus);
        GLM.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        if (CONSECRATION) {
            bus.addListener(this::enqueueIMC);
        }
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Network::register);
        Raid.RaiderType.create(MUSKET_PILLAGER.get().toString(), MUSKET_PILLAGER.get(), new int[]{0, 2, 2, 2, 3, 3, 3, 4, 4});
    }

    public void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", () -> (BiFunction<LivingEntity, DamageSource, Boolean>)
                (livingEntity, damageSource) -> (Config.CONSECRATION_COMPAT.get() && damageSource.getDirectEntity() instanceof BulletEntity bullet && bullet.isHoly()));
    }
}