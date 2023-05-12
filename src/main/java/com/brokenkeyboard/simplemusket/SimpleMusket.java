package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.BastionLoot.BastionLootSerializer;
import com.brokenkeyboard.simplemusket.datagen.PiglinBarter.PiglinBarterSerializer;
import com.brokenkeyboard.simplemusket.enchantment.DeadeyeEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.FirepowerEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.LongshotEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.RepeatingEnchantment;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
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

@Mod(SimpleMusket.MOD_ID)
public class SimpleMusket
{
    public static final String MOD_ID = "simplemusket";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MOD_ID);

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

    public static final RegistryObject<BastionLootSerializer> BASTION_LOOT = GLM.register("bastion_loot", BastionLootSerializer::new);
    public static final RegistryObject<PiglinBarterSerializer> PIGLIN_BARTER = GLM.register("piglin_barter", PiglinBarterSerializer::new);
    public static final RegistryObject<MusketItem> MUSKET = ITEMS.register("musket", () -> new MusketItem(new net.minecraft.world.item.Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_BULLET = ITEMS.register("iron_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 1));
    public static final RegistryObject<Item> COPPER_BULLET = ITEMS.register("copper_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 2));
    public static final RegistryObject<Item> GOLD_BULLET = ITEMS.register("gold_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 3));
    public static final RegistryObject<Item> NETHERITE_BULLET = ITEMS.register("netherite_bullet", () -> new BulletItem(new net.minecraft.world.item.Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 4));

    public static final RegistryObject<Enchantment> FIREPOWER = ENCHANTMENTS.register("firepower", () -> new FirepowerEnchantment(
            Enchantment.Rarity.COMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DEADEYE = ENCHANTMENTS.register("deadeye", () -> new DeadeyeEnchantment(
            Enchantment.Rarity.UNCOMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> LONGSHOT = ENCHANTMENTS.register("longshot", () -> new LongshotEnchantment(
            Enchantment.Rarity.RARE, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> REPEATING = ENCHANTMENTS.register("repeating", () -> new RepeatingEnchantment(
            Enchantment.Rarity.VERY_RARE, FIREARM, EquipmentSlot.MAINHAND));

    public SimpleMusket() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.registerConfig();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        ENCHANTMENTS.register(bus);
        GLM.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        if (ModList.get().isLoaded("consecration"))
            bus.addListener(this::enqueueIMC);
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", ()
                -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
            if(damageSource.getDirectEntity() != null && damageSource.getDirectEntity() instanceof BulletEntity bullet && Config.CONSECRATION_COMPAT.get()) {
                return bullet.isMagicBullet();
            }
            return false;
        });
    }

    public void setup(final FMLCommonSetupEvent event) {
        int[] intArray = new int[9];
        for (int i = 0; i < 9; i++) {
            intArray[i] = i < 4 ? 2 : 3;
        }
        SpawnPlacements.register(MUSKET_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        Raid.RaiderType.create(MUSKET_PILLAGER.get().toString(), MUSKET_PILLAGER.get(), intArray);
    }
}