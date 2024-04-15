package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.datagen.conditions.EnchantedCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.HellfireCondition;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider implements IConditionBuilder {

    public Recipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModRegistry.MUSKET)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('F', Items.FLINT_AND_STEEL)
                .define('P', ItemTags.PLANKS)
                .pattern("I  ")
                .pattern("PIF")
                .pattern(" PI")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .unlockedBy("has_flint_and_steel", has(Items.FLINT_AND_STEEL))
                .save(consumer);

        cartridgeRecipe(ModRegistry.CARTRIDGE, Items.IRON_INGOT, 4, TRUE(), consumer);
        cartridgeRecipe(ModRegistry.ENCHANTED_CARTRIDGE, Items.GOLD_INGOT, 4, new EnchantedCondition(), consumer);
        cartridgeRecipe(ModRegistry.HELLFIRE_CARTRIDGE, Items.NETHERITE_INGOT, 8, new HellfireCondition(), consumer);
    }

    private void cartridgeRecipe(Item bulletItem, Item ingredient, int amount, ICondition condition, Consumer<FinishedRecipe> consumer) {
        ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, bulletItem.toString());

        ConditionalRecipe.builder().addCondition(condition).addRecipe(
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
                        ::save
        ).generateAdvancement().build(consumer, ID);
    }
}