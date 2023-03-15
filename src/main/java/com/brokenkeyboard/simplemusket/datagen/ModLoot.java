package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.function.Supplier;

public class ModLoot extends LootModifier {
    public static final Supplier<Codec<ModLoot>> CODEC = Suppliers.memoize(()
            -> RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
            .fieldOf("item").forGetter(m -> m.item)).apply(inst, ModLoot::new)));

    private final Item item;

    public ModLoot(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.FIND_NETHERITE_BULLETS.get()) return generatedLoot;

        int max = Config.MAX_NETHERITE_BULLETS.get();
        int min = (int)Math.ceil((double)max / 2);

        Random rand = new Random();
        double rng = rand.nextDouble();

        if (rng < 0.3) {
            int stackCount = rand.nextInt(max - min + 1) + min;
            generatedLoot.add(new ItemStack(item, stackCount));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}