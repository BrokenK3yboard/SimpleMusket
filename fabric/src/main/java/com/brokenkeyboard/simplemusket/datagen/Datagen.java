package com.brokenkeyboard.simplemusket.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class Datagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
            fabricDatagen(generator.createPack());
    }

    public static void fabricDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(EntityTags::new);
        pack.addProvider(Recipes::new);
    }
}