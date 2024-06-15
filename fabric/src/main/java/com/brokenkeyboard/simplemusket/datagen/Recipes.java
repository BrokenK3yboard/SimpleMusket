package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class Recipes extends FabricRecipeProvider {

    public Recipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {

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
                .save(exporter);

        bulletRecipe(ModRegistry.CARTRIDGE, Items.IRON_INGOT, 4, exporter, Constants.CRAFT_CARTRIDGE);
        bulletRecipe(ModRegistry.HELLFIRE_CARTRIDGE, Items.NETHERITE_INGOT, 8, exporter, Constants.CRAFT_HELLFIRE);
    }

    private void bulletRecipe(Item bulletItem, Item ingredient, int amount, RecipeOutput exporter, ResourceLocation location) {

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
                .save(withConditions(exporter, Conditions.configBooleans(location, String.valueOf(location))));
    }
}