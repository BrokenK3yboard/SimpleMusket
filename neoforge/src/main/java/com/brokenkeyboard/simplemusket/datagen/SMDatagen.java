package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class SMDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        SMDataPackProvider provider = new SMDataPackProvider(output, event.getLookupProvider());
        CompletableFuture<HolderLookup.Provider> registries = provider.getRegistryProvider();

        if (Boolean.getBoolean("commonData")) {
            generator.addProvider(true, provider);
            generator.addProvider(true, new SMDamageTypeProvider(output, registries));
            generator.addProvider(true, new SMEntityTypeProvider(output, registries));
            generator.addProvider(true, new SMItemProvider(output, registries));
            generator.addProvider(true, new SMEnchantmentProvider(output, registries));
            generator.addProvider(true, new SMVillagerTradesProvider(output, registries));
            generator.addProvider(true, new LootTableProvider(output, Set.of(),
                    List.of(new LootTableProvider.SubProviderEntry(SMLootProvider::new, LootContextParamSets.ENTITY)), registries));
            event.addProvider(new SMRecipeProvider.Runner(output, registries));
            generator.addProvider(true, new SMModelGenerator(output));
        } else {
            generator.addProvider(true, new SMLootModifierProvider(output, registries));
        }
    }
}