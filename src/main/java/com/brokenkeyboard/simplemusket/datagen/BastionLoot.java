package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BastionLoot extends LootModifier {

    public BastionLoot(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!Config.FIND_NETHERITE_BULLETS.get()) return generatedLoot;

        int max = Config.MAX_NETHERITE_BULLETS.get();
        int min = (int)Math.ceil((double)max / 2);

        Random rand = new Random();
        double rng = rand.nextDouble();

        if (rng < 0.3) {
            int stackCount = rand.nextInt(max - min + 1) + min;
            generatedLoot.add(new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), stackCount));
        }
        return generatedLoot;
    }

    public static class BastionLootSerializer extends GlobalLootModifierSerializer<BastionLoot> {

        @Override
        public BastionLoot read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new BastionLoot(ailootcondition);
        }

        @Override
        public JsonObject write(BastionLoot instance) {
            return new JsonObject();
        }
    }
}