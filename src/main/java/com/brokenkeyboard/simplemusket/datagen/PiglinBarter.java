package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PiglinBarter extends LootModifier {

    public static final Supplier<Codec<PiglinBarter>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, PiglinBarter::new)));

    public PiglinBarter(LootItemCondition[] condition) {
        super(condition);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(!Config.BARTER_NETHERITE_BULLETS.get()) return generatedLoot;

        RandomSource random = context.getRandom();
        double rng = random.nextDouble();
        if (generatedLoot.size() == 1 && generatedLoot.get(0).getItem() == Items.SPECTRAL_ARROW && rng < 0.25) {
            int amount = random.nextInt(2) + 2;
            generatedLoot.set(0, new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), amount));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}