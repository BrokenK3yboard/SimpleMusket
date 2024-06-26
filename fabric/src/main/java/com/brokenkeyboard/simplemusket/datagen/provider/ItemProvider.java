package com.brokenkeyboard.simplemusket.datagen.provider;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ItemProvider extends ItemTagsProvider {

    public ItemProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> completableFuture2) {
        super(packOutput, completableFuture, completableFuture2);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModRegistry.MUSKET_ENCHANTABLE).add(ModRegistry.MUSKET);
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(ModRegistry.MUSKET);
    }
}