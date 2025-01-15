package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class EnchCartridgeRecipe extends CustomRecipe {

    public EnchCartridgeRecipe(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        if (container.getItems().size() < 3) return false;
        int book = 0, gold = 0, cartridge = 0;

        for (int i = 0; i < container.getItems().size(); ++i) {
            ItemStack stack = container.getItem(i);
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.getItems().size(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack itemstack = container.getItem(i);
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
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        return new ItemStack(ModRegistry.ENCHANTED_CARTRIDGE, (int) (container.getItems().stream()
                .filter(stack -> !stack.is(ItemStack.EMPTY.getItem())).count() - 1) / 2);
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
