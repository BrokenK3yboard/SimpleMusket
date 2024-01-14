package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.SimpleMusket.BULLET;

@Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, Datagen::bootstrap);

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), BUILDER, Set.of(SimpleMusket.MOD_ID));
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
        context.register(BULLET, new DamageType("bullet", 0.5F));
    }
}