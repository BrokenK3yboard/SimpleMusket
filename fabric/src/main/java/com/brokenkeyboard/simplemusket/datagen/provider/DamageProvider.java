package com.brokenkeyboard.simplemusket.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.ModRegistry.BULLET;

public class DamageProvider extends DamageTypeTagsProvider {

    public DamageProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_SHIELD).add(BULLET);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(BULLET);
    }
}