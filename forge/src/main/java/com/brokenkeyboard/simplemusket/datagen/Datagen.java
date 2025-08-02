package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        if (event.includeServer()) {
            if (event.getInputs().contains(Path.of("commonData"))) {
                DataPackProvider provider = new DataPackProvider(output, event.getLookupProvider());
                CompletableFuture<HolderLookup.Provider> lookupProvider = provider.getRegistryProvider();
                generator.addProvider(true, provider);
                generator.addProvider(true, new DamageTags(output, lookupProvider, event.getExistingFileHelper()));
                generator.addProvider(true, new EntityTags(output, lookupProvider, event.getExistingFileHelper()));
            } else {
                generator.addProvider(true, new Recipes(output));
                generator.addProvider(true, new GLMProvider(output));
            }
        }
    }
}