package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class GLMProvider extends GlobalLootModifierProvider {
    public GLMProvider(DataGenerator generator) {
        super(generator, SimpleMusket.MOD_ID);
    }

    @Override
    protected void start() {
        add("bastion_loot", SimpleMusket.BASTION_LOOT.get(), new BastionLoot(new LootItemCondition[] {
                new AlternativeLootItemCondition.Builder(
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_other")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_hoglin_stable")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_treasure")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_bridge"))
                ).build()
        }));

        add("piglin_barter", SimpleMusket.PIGLIN_BARTER.get(), new PiglinBarter(new LootItemCondition[] {
                new AlternativeLootItemCondition.Builder(new LootTableIdCondition.Builder(new ResourceLocation("minecraft:gameplay/piglin_bartering"))).build()
        }));
    }
}