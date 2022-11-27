package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ModLoot extends LootModifier {
    private final int min;
    private final int max;
    private final double chance;

    public ModLoot(LootItemCondition[] conditionsIn, final int min, final int max, final double chance) {
        super(conditionsIn);
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!Config.FIND_NETHERITE_BULLETS.get()) return generatedLoot;

        Random rand = new Random();
        double rng = rand.nextDouble();
        if(rng <= chance) {
            int stackCount = rand.nextInt(max - min + 1) + min;
            generatedLoot.add(new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), stackCount));
        }
        return generatedLoot;
    }

    public static class ModLootSerializer extends GlobalLootModifierSerializer<ModLoot> {

        @Override
        public ModLoot read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            final int min = GsonHelper.getAsInt(object, "min");
            final int max = GsonHelper.getAsInt(object, "max");
            final double chance = GsonHelper.getAsDouble(object, "chance");
            return new ModLoot(ailootcondition, min, max, chance);
        }

        @Override
        public JsonObject write(ModLoot instance) {
            return new JsonObject();
        }
    }
}