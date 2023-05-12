package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class PiglinBarter extends LootModifier {

    public PiglinBarter(LootItemCondition[] condition) {
        super(condition);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Random rand = new Random();
        double rng = rand.nextDouble();

        if (generatedLoot.size() == 1 && generatedLoot.get(0).getItem() == Items.SPECTRAL_ARROW && rng < 0.25) {
            int amount = rand.nextInt(2) + 2;
            generatedLoot.set(0, new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), amount));
        }
        return generatedLoot;
    }

    public static class PiglinBarterSerializer extends GlobalLootModifierSerializer<PiglinBarter> {

        @Override
        public PiglinBarter read(ResourceLocation location, JsonObject object, LootItemCondition[] condition) {
            return new PiglinBarter(condition);
        }

        @Override
        public JsonObject write(PiglinBarter instance) {
            return new JsonObject();
        }
    }
}