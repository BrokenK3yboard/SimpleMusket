package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class SMLootModifierProvider extends GlobalLootModifierProvider {

    public SMLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    protected void start() {
        add("skeleton_loot", new SMSkeletonLootModifier(new LootItemCondition[] {
                new AnyOfCondition.Builder(new LootTableIdCondition.Builder(Identifier.withDefaultNamespace("entities/skeleton"))).build()
        }, 1000));
    }
}