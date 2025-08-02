package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EnchantProvider extends EnchantmentTagsProvider {

    public EnchantProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper fileHelper) {
        super(output, registries, ModRegistry.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModRegistry.MUSKET_EXCLUSIVE).add(ModRegistry.FIREPOWER).add(ModRegistry.LONGSHOT).add(ModRegistry.REPEATING);
        tag(EnchantmentTags.NON_TREASURE).add(ModRegistry.FIREPOWER).add(ModRegistry.LONGSHOT).add(ModRegistry.REPEATING);
    }
}
