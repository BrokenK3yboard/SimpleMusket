package com.brokenkeyboard.simplemusket.datagen.provider;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class EnchantProvider extends EnchantmentTagsProvider {

    public EnchantProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModRegistry.MUSKET_EXCLUSIVE).add(ModRegistry.FIREPOWER).add(ModRegistry.LONGSHOT).add(ModRegistry.REPEATING);
        tag(EnchantmentTags.NON_TREASURE).add(ModRegistry.FIREPOWER).add(ModRegistry.LONGSHOT).add(ModRegistry.REPEATING);
    }
}
