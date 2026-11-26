package com.brokenkeyboard.simplemusket.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VillagerTradesTagsProvider;
import net.minecraft.tags.VillagerTradeTags;

import java.util.concurrent.CompletableFuture;

public class SMVillagerTradesProvider extends VillagerTradesTagsProvider {

    public SMVillagerTradesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.tag(VillagerTradeTags.WEAPONSMITH_LEVEL_3)
                .add(SMDataPackProvider.WEAPONSMITH_3_EMERALD_MUSKET)
                .add(SMDataPackProvider.WEAPONSMITH_3_EMERALD_CARTRIDGE);
    }
}
