package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class GLMProvider extends GlobalLootModifierProvider {

    public GLMProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void start() {
        add("bastion_loot", new BastionLoot(new LootItemCondition[] {
                new AnyOfCondition.Builder(
                        new LootTableIdCondition.Builder(Constants.BASTION_OTHER),
                        new LootTableIdCondition.Builder(Constants.BASTION_TREASURE),
                        new LootTableIdCondition.Builder(Constants.BASTION_BRIDGE)
                ).build()
        }));

        add("piglin_barter", new PiglinBarter(new LootItemCondition[] {
                new AnyOfCondition.Builder(new LootTableIdCondition.Builder(Constants.PIGLIN_BARTER)).build()
        }));
    }
}