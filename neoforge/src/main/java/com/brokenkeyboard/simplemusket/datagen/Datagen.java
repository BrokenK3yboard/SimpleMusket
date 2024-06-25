package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), BUILDER, Set.of(Constants.MOD_ID));
        CompletableFuture<HolderLookup.Provider> lookupProvider = provider.getRegistryProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new GLMProvider(output, lookupProvider));
        }
    }
}