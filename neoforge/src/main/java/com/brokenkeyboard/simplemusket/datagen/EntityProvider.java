package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EntityProvider extends EntityTypeTagsProvider {

    public EntityProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper fileHelper) {
        super(output, registries, ModRegistry.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(EntityTypeTags.ILLAGER).add(ModRegistry.GUNSLINGER);
        this.tag(EntityTypeTags.RAIDERS).add(ModRegistry.GUNSLINGER);
        this.tag(EntityTypeTags.ILLAGER_FRIENDS).add(ModRegistry.GUNSLINGER);
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.BULLET_ENTITY);
    }
}
