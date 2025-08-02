package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataPackProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DataPackProvider::damageTypeBootstrap)
            .add(Registries.STRUCTURE, DataPackProvider::structureBootstrap);

    public DataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Constants.MOD_ID, "minecraft"));
    }

    protected static void damageTypeBootstrap(BootstapContext<DamageType> context) {
        context.register(Constants.BULLET, Constants.BULLET_DAMAGE_TYPE);
    }

    protected static void structureBootstrap(BootstapContext<Structure> context) {
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
