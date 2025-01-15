package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    static final ResourceKey<BiomeModifier> GUNSLINGER_SPAWNS = ResourceKey.create(
            ForgeRegistries.Keys.BIOME_MODIFIERS,
            new ResourceLocation(Constants.MOD_ID, "gunslinger_spawn"));

    static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, Datagen::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
                context.register(GUNSLINGER_SPAWNS, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biome.getOrThrow(
                        biome.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST).key()),
                        List.of(new MobSpawnSettings.SpawnerData(ModRegistry.GUNSLINGER, 15, 1, 1))));
            });

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), BUILDER, Set.of(Constants.MOD_ID));
        CompletableFuture<HolderLookup.Provider> lookupProvider = provider.getRegistryProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(output));
            generator.addProvider(true, provider);
            generator.addProvider(true, new DamageTags(output, lookupProvider, event.getExistingFileHelper()));
            generator.addProvider(true, new EntityTags(output, lookupProvider, event.getExistingFileHelper()));
            generator.addProvider(true, new GLMProvider(output));
        }
    }

    protected static void bootstrap(BootstapContext<DamageType> context) {
        context.register(Constants.BULLET, Constants.BULLET_DAMAGE_TYPE);
    }
}