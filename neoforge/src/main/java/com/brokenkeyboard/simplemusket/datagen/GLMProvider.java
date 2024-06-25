package com.brokenkeyboard.simplemusket.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.Constants.*;

public class GLMProvider extends GlobalLootModifierProvider {

    public GLMProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MOD_ID);
    }

    @Override
    protected void start() {
        add("bastion_loot", new BastionLoot(new LootItemCondition[] {
                new AnyOfCondition.Builder(
                        new LootTableIdCondition.Builder(BASTION_OTHER),
                        new LootTableIdCondition.Builder(BASTION_TREASURE),
                        new LootTableIdCondition.Builder(BASTION_BRIDGE)
                ).build()
        }));

        add("piglin_barter", new PiglinBarter(new LootItemCondition[] {
                new AnyOfCondition.Builder(new LootTableIdCondition.Builder(PIGLIN_BARTER)).build()
        }));
    }
}