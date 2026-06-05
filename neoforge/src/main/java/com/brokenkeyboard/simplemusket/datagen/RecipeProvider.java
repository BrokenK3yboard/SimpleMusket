package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModRegistry.MUSKET)
                .define('I', Items.IRON_INGOT)
                .define('F', Items.FLINT_AND_STEEL)
                .define('P', ItemTags.PLANKS)
                .pattern("I  ")
                .pattern("PIF")
                .pattern(" PI")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .unlockedBy("has_flint_and_steel", has(Items.FLINT_AND_STEEL))
                .save(output);

        cartridgeRecipe(ModRegistry.CARTRIDGE, Items.IRON_INGOT, 8, output);
        cartridgeRecipe(ModRegistry.ENCHANTED_CARTRIDGE, Items.GOLD_INGOT, 4, output);
    }

    private void cartridgeRecipe(Item bulletItem, Item ingredient, int amount, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, bulletItem, amount)
                .define('C', ingredient)
                .define('G', Items.GUNPOWDER)
                .define('P', Items.PAPER)
                .pattern(" C ")
                .pattern(" G ")
                .pattern(" P ")
                .unlockedBy("has_" + ingredient.toString().toLowerCase(), has(ingredient))
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
    }
}