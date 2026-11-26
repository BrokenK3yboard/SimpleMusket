package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class SMRecipeProvider extends RecipeProvider {

    private final HolderLookup.Provider registries;

    protected SMRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes() {
        HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, ModRegistry.MUSKET.value())
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

        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, ModRegistry.CARTRIDGE.value(), 8)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GUNPOWDER)
                .define('P', Items.PAPER)
                .pattern(" I ")
                .pattern(" G ")
                .pattern(" P ")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected SMRecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new SMRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "ModRecipeProvider";
        }
    }

}