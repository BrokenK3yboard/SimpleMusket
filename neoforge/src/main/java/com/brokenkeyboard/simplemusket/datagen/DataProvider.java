package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataProvider extends DatapackBuiltinEntriesProvider {

    public static final ResourceKey<BiomeModifier> GUNSLINGER_SPAWNS = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ModRegistry.location("gunslinger_spawn"));

    public DataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Datagen.BUILDER, Collections.singleton(ModRegistry.MOD_ID));
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biome = context.lookup(Registries.BIOME);

        context.register(GUNSLINGER_SPAWNS, new BiomeModifiers.AddSpawnsBiomeModifier(
                biome.getOrThrow(biome.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST).key()),
                List.of(new MobSpawnSettings.SpawnerData(ModRegistry.GUNSLINGER, 60, 1, 1))
        ));
    }
}
