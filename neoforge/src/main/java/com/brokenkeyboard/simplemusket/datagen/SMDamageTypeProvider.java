package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class SMDamageTypeProvider extends DamageTypeTagsProvider {

    public SMDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_SHIELD).add(ModRegistry.BULLET);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(ModRegistry.BULLET);
    }
}