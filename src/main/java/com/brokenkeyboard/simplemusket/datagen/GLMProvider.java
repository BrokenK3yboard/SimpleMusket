package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class GLMProvider extends GlobalLootModifierProvider {

    public GLMProvider(PackOutput output) {
        super(output, SimpleMusket.MOD_ID);
    }

    @Override
    protected void start() {
        add("bastion_loot", new BastionLoot(new LootItemCondition[] {
                new AnyOfCondition.Builder(
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_other")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_hoglin_stable")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_treasure")),
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft:chests/bastion_bridge"))
                ).build()
        }));

        add("piglin_barter", new PiglinBarter(new LootItemCondition[] {
                new AnyOfCondition.Builder(new LootTableIdCondition.Builder(new ResourceLocation("minecraft:gameplay/piglin_bartering"))).build()
        }));
    }
}