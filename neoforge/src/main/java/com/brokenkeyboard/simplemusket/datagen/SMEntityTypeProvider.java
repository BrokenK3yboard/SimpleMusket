package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class SMEntityTypeProvider extends EntityTypeTagsProvider {

    public SMEntityTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.ILLAGER).add(ModRegistry.GUNSLINGER.value());
        this.tag(EntityTypeTags.RAIDERS).add(ModRegistry.GUNSLINGER.value());
        this.tag(EntityTypeTags.ILLAGER_FRIENDS).add(ModRegistry.GUNSLINGER.value());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.BULLET_ENTITY.value());
    }
}
