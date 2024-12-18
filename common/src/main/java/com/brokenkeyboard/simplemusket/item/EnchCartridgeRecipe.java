package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class EnchCartridgeRecipe extends CustomRecipe {

    public EnchCartridgeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() < 3) return false;
        int book = 0, gold = 0, cartridge = 0;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.ENCHANTED_BOOK)) {
                book++;
            } else if (stack.is(Items.GOLD_NUGGET)) {
                gold++;
            } else if (stack.is(ModRegistry.CARTRIDGE)) {
                cartridge++;
            }
        }
        return book == 1 && gold == cartridge;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack itemstack = input.getItem(i);
            if (itemstack.getItem().getCraftingRemainingItem() != null) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
            } else if (itemstack.is(Items.ENCHANTED_BOOK)) {
                nonnulllist.set(i, new ItemStack(Items.BOOK));
                break;
            }
        }
        return nonnulllist;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(ModRegistry.ENCHANTED_CARTRIDGE, (input.ingredientCount() - 1) / 2);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.ENCH_CARTRIDGE_CRAFTING;
    }
}
