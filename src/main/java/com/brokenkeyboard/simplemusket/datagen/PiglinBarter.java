package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
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

    public PiglinBarter(LootItemCondition[] conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if(!Config.BARTER_NETHERITE_BULLETS.get()) return generatedLoot;

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
        public PiglinBarter read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            return new PiglinBarter(conditions);
        }

        @Override
        public JsonObject write(PiglinBarter instance) {
            return this.makeConditions(instance.conditions);
        }
    }
}