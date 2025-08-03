package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.AmmoCountEffect;
import com.brokenkeyboard.simplemusket.enchantment.ComponentKillEffect;
import com.brokenkeyboard.simplemusket.enchantment.DamageDistanceEffect;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.ModRegistry.*;

public class DataPackProvider extends DatapackBuiltinEntriesProvider {

    public static final DamageType BULLET = new DamageType("bullet", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.5F);

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DataPackProvider::damageTypeBC)
            .add(Registries.ENCHANTMENT, DataPackProvider::enchantmentBC)
            .add(Registries.ENCHANTMENT_PROVIDER, DataPackProvider::enchantProviderBC)
            .add(Registries.STRUCTURE, DataPackProvider::structureBootstrap);

    public DataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ModRegistry.MOD_ID, "minecraft"));
    }

    protected static void damageTypeBC(BootstrapContext<DamageType> context) {
        context.register(ModRegistry.BULLET, BULLET);
    }

    protected static void enchantmentBC(BootstrapContext<Enchantment> context) {
        HolderSet.Named<Item> itemHolder = context.lookup(Registries.ITEM).getOrThrow(MUSKET_ENCHANTABLE);
        HolderGetter<Enchantment> enchHolder = context.lookup(Registries.ENCHANTMENT);

        context.register(FIREPOWER, Enchantment.enchantment(Enchantment.definition(itemHolder, 10, 5, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.1F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY).build()))
                .build(FIREPOWER.location()));

        context.register(LONGSHOT, Enchantment.enchantment(Enchantment.definition(itemHolder, 2, 2, Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), 4, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(DAMAGE_DISTANCE, new DamageDistanceEffect(0.15F, LevelBasedValue.perLevel(1.0F, 0.66F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY).build()))
                .build(LONGSHOT.location()));

        context.register(REPEATING, Enchantment.enchantment(Enchantment.definition(itemHolder, 1, 1, Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(AMMO_COUNT, new AmmoCountEffect(LevelBasedValue.constant(1F), LevelBasedValue.constant(1F)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new ComponentKillEffect())
                .build(REPEATING.location()));
    }

    protected static void enchantProviderBC(BootstrapContext<EnchantmentProvider> context) {
        HolderGetter<Enchantment> holder = context.lookup(Registries.ENCHANTMENT);
        context.register(GUNSLINGER_SPAWN_MUSKET, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(RAID_GUNSLINGER_POST_WAVE_3, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(RAID_GUNSLINGER_POST_WAVE_5, new SingleEnchantment(holder.getOrThrow(LONGSHOT), ConstantInt.of(1)));
    }

    protected static void structureBootstrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomeHolder = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePoolHolder = context.lookup(Registries.TEMPLATE_POOL);

        context.register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(
                structure(
                        biomeHolder.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST),
                        Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                WeightedRandomList.create(
                                        new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 5, 1, 1),
                                        new MobSpawnSettings.SpawnerData(ModRegistry.GUNSLINGER, 1, 1, 1)))
                        ),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.BEARD_THIN
                ),
                templatePoolHolder.getOrThrow(PillagerOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG)
        );
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
        return new Structure.StructureSettings(biomes, spawnOverrides, step, terrainAdaptation);
    }
}