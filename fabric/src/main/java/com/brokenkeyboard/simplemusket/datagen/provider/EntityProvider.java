package com.brokenkeyboard.simplemusket.datagen.provider;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EntityProvider extends EntityTypeTagsProvider {

    public EntityProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(EntityTypeTags.ILLAGER).add(ModRegistry.GUNSLINGER);
        this.tag(EntityTypeTags.RAIDERS).add(ModRegistry.GUNSLINGER);
        this.tag(EntityTypeTags.ILLAGER_FRIENDS).add(ModRegistry.GUNSLINGER);
    }
}
