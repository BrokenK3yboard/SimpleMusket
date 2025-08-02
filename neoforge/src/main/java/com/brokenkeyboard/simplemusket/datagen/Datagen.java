package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModRegistry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        DataPackProvider provider = new DataPackProvider(output, event.getLookupProvider());
        CompletableFuture<HolderLookup.Provider> registries = provider.getRegistryProvider();

        if (event.includeServer()) {
            if (event.getInputs().contains(Path.of("commonData"))) {
                generator.addProvider(true, provider);
                BlockProvider blockTags = generator.addProvider(event.includeServer(), new BlockProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
                generator.addProvider(true, new DamageProvider(output, registries, event.getExistingFileHelper()));
                generator.addProvider(true, new RecipeProvider(output, registries));
                generator.addProvider(true, new EntityProvider(output, registries, event.getExistingFileHelper()));
                generator.addProvider(true, new ItemProvider(output, registries, blockTags.contentsGetter()));
                generator.addProvider(true, new EnchantProvider(output, registries, event.getExistingFileHelper()));
            } else {
                generator.addProvider(true, new GLMProvider(output, registries));
            }
        }
    }
}