package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;

public class Datagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
            fabricDatagen(generator.createPack());
    }

    public static void fabricDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(RegistryProvider::new);
        pack.addProvider(DamageTags::new);
        pack.addProvider(EntityTags::new);
        pack.addProvider(Recipes::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.DAMAGE_TYPE, Datagen::damageTypeBC);
    }

    protected static void damageTypeBC(BootstapContext<DamageType> context) {
        context.register(Constants.BULLET, Constants.BULLET_DAMAGE_TYPE);
    }
}