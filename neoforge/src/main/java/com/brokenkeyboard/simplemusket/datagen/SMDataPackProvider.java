package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.AmmoCountEffect;
import com.brokenkeyboard.simplemusket.enchantment.AmmoBonusEffect;
import com.brokenkeyboard.simplemusket.enchantment.DamageDistanceEffect;
import com.brokenkeyboard.simplemusket.enchantment.FullyAimedPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SMDataPackProvider extends DatapackBuiltinEntriesProvider {

    public static final TagKey<Item> MUSKET_ENCHANTABLE = TagKey.create(Registries.ITEM, ModRegistry.location("enchantable/musket"));
    public static final DamageType BULLET = new DamageType("bullet", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5F);
    public static final TagKey<Enchantment> MUSKET_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, ModRegistry.location("exclusive_set/musket"));
    public static final ResourceKey<Enchantment> FIREPOWER = ResourceKey.create(Registries.ENCHANTMENT, ModRegistry.location("firepower"));
    public static final ResourceKey<Enchantment> LONGSHOT = ResourceKey.create(Registries.ENCHANTMENT, ModRegistry.location("longshot"));
    public static final ResourceKey<Enchantment> REPEATING = ResourceKey.create(Registries.ENCHANTMENT, ModRegistry.location("repeating"));
    public static final ResourceKey<VillagerTrade> WEAPONSMITH_3_EMERALD_MUSKET = ResourceKey.create(Registries.VILLAGER_TRADE, ModRegistry.location("weaponsmith/3/emerald_musket"));
    public static final ResourceKey<VillagerTrade> WEAPONSMITH_3_EMERALD_CARTRIDGE = ResourceKey.create(Registries.VILLAGER_TRADE, ModRegistry.location("weaponsmith/3/emerald_cartridge"));

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, SMDataPackProvider::damageTypes)
            .add(Registries.ENCHANTMENT, SMDataPackProvider::enchantments)
            .add(Registries.ENCHANTMENT_PROVIDER, SMDataPackProvider::enchantmentProvider)
            .add(Registries.VILLAGER_TRADE, SMDataPackProvider::trades)
            .add(Registries.STRUCTURE, SMDataPackProvider::structures);

    public SMDataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Constants.MOD_ID, "minecraft"));
    }

    protected static void damageTypes(BootstrapContext<DamageType> context) {
        context.register(ModRegistry.BULLET, BULLET);
    }

    protected static void enchantments(BootstrapContext<Enchantment> context) {
        HolderSet.Named<Item> itemHolder = context.lookup(Registries.ITEM).getOrThrow(MUSKET_ENCHANTABLE);
        HolderGetter<Enchantment> enchHolder = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<EntityType<?>> entityTypes = context.lookup(Registries.ENTITY_TYPE);

        context.register(FIREPOWER, Enchantment.enchantment(Enchantment.definition(itemHolder, 10, 4, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.1F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(entityTypes, ModRegistry.BULLET_ENTITY.value()).subPredicate(FullyAimedPredicate.FULLY_AIMED).build()))
                .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(1)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(entityTypes, ModRegistry.BULLET_ENTITY.value()).subPredicate(FullyAimedPredicate.FULLY_AIMED).build()))
                .build(ModRegistry.location("firepower")));

        context.register(LONGSHOT, Enchantment.enchantment(Enchantment.definition(itemHolder, 2, 2, Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), 4, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(ModRegistry.DAMAGE_DISTANCE.value(), new DamageDistanceEffect(0.25F, LevelBasedValue.perLevel(1, 0.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(entityTypes, ModRegistry.BULLET_ENTITY.value()).build()))
                .build(ModRegistry.location("longshot")));

        context.register(REPEATING, Enchantment.enchantment(Enchantment.definition(itemHolder, 1, 1, Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(ModRegistry.AMMO_COUNT.value(), new AmmoCountEffect(LevelBasedValue.constant(1)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new AmmoBonusEffect(LevelBasedValue.constant(2)))
                .build(ModRegistry.location("repeating")));
    }

    protected static void enchantmentProvider(BootstrapContext<EnchantmentProvider> context) {
        HolderGetter<Enchantment> holder = context.lookup(Registries.ENCHANTMENT);
        context.register(ModRegistry.GUNSLINGER_SPAWN_MUSKET, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(ModRegistry.RAID_GUNSLINGER_POST_WAVE_3, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(ModRegistry.RAID_GUNSLINGER_POST_WAVE_5, new SingleEnchantment(holder.getOrThrow(LONGSHOT), ConstantInt.of(1)));
    }

    protected static void trades(BootstrapContext<VillagerTrade> context) {
        context.register(WEAPONSMITH_3_EMERALD_MUSKET, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(ModRegistry.MUSKET.value()), 4, 15, 0.05F, Optional.empty(), List.of()));
        context.register(WEAPONSMITH_3_EMERALD_CARTRIDGE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(ModRegistry.CARTRIDGE.value(), 8), 8, 2, 0.05F, Optional.empty(), List.of()));
    }

    protected static void structures(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templates = context.lookup(Registries.TEMPLATE_POOL);

        context.register(
                BuiltinStructures.PILLAGER_OUTPOST,
                new JigsawStructure(
                        new Structure.StructureSettings.Builder(biomes.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST))
                                .spawnOverrides(
                                        Map.of(MobCategory.MONSTER,
                                                new StructureSpawnOverride(
                                                        StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedList.of(
                                                                new Weighted<>(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1), 8),
                                                        new Weighted<>(new MobSpawnSettings.SpawnerData(ModRegistry.GUNSLINGER.value(), 1, 1), 1))
                                                )
                                        )
                                )
                                .terrainAdapation(TerrainAdjustment.BEARD_THIN)
                                .build(),
                        templates.getOrThrow(PillagerOutpostPools.START),
                        7,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        true,
                        Heightmap.Types.WORLD_SURFACE_WG
                )
        );
    }
}