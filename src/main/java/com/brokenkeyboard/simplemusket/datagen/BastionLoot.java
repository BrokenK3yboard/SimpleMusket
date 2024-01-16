package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BastionLoot extends LootModifier {

    public static final Supplier<Codec<BastionLoot>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, BastionLoot::new)));

    public BastionLoot(LootItemCondition[] conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!Config.FIND_NETHERITE_BULLETS.get()) return generatedLoot;

        int max = Config.MAX_NETHERITE_BULLETS.get();
        int min = (int)Math.ceil((double)max / 2);

        RandomSource random = context.getRandom();
        double rng = random.nextDouble();

        if (rng < 0.3) {
            int stackCount = random.nextInt(max - min + 1) + min;
            generatedLoot.add(new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), stackCount));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}