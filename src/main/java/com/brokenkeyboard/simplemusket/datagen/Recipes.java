package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.BulletItem;
import com.brokenkeyboard.simplemusket.BulletType;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.datagen.conditions.CopperCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.GoldCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.NetheriteCondition;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
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

import java.util.function.Consumer;

public class Recipes extends RecipeProvider implements IConditionBuilder {

    public Recipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(SimpleMusket.MUSKET.get())
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

        bulletRecipe((BulletItem) SimpleMusket.IRON_BULLET.get(), Items.IRON_INGOT, 4, consumer);
        bulletRecipe((BulletItem) SimpleMusket.COPPER_BULLET.get(), Items.COPPER_INGOT, 4, consumer);
        bulletRecipe((BulletItem) SimpleMusket.GOLD_BULLET.get(), Items.GOLD_INGOT, 4, consumer);
        bulletRecipe((BulletItem) SimpleMusket.NETHERITE_BULLET.get(), Items.NETHERITE_INGOT, 4, consumer);
    }

    private void bulletRecipe(BulletItem bulletItem, Item ingredient, int amount, Consumer<FinishedRecipe> consumer) {

        ResourceLocation ID = new ResourceLocation(SimpleMusket.MOD_ID, bulletItem.toString());

        ConditionalRecipe.builder().addCondition(
                getCondition(BulletType.values()[bulletItem.getType()])
        ).addRecipe(
                ShapedRecipeBuilder.shaped(bulletItem, amount)
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

    private ICondition getCondition(BulletType type) {
        return switch(type) {
            case COPPER -> new CopperCondition();
            case GOLD -> new GoldCondition();
            case NETHERITE -> new NetheriteCondition();
            default -> TRUE();
        };
    }
}