package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class SMEnchantmentProvider extends EnchantmentTagsProvider {

    public SMEnchantmentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(SMDataPackProvider.MUSKET_EXCLUSIVE).add(SMDataPackProvider.FIREPOWER).add(SMDataPackProvider.LONGSHOT).add(SMDataPackProvider.REPEATING);
        tag(EnchantmentTags.NON_TREASURE).add(SMDataPackProvider.FIREPOWER).add(SMDataPackProvider.LONGSHOT).add(SMDataPackProvider.REPEATING);
    }
}
