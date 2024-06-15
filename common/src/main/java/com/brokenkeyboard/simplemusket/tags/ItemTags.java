package com.brokenkeyboard.simplemusket.tags;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {

    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockProvider) {
        super(output, provider, blockProvider);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(ModRegistry.FIREARM).add(ModRegistry.MUSKET);
    }
}