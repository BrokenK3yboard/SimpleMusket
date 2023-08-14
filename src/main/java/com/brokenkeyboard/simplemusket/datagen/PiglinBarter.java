package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class PiglinBarter extends LootModifier {
    public static final Supplier<Codec<PiglinBarter>> CODEC = Suppliers.memoize(()
            -> RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
            .fieldOf("item").forGetter(m -> m.item)).apply(inst, PiglinBarter::new)));

    private final Item item;

    public PiglinBarter(LootItemCondition[] condition, Item item) {
        super(condition);
        this.item = item;
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(!Config.BARTER_NETHERITE_BULLETS.get()) return generatedLoot;

        Random rand = new Random();
        double rng = rand.nextDouble();
        if (generatedLoot.size() == 1 && generatedLoot.get(0).getItem() == Items.SPECTRAL_ARROW && rng < 0.25) {
            int amount = rand.nextInt(2) + 2;
            generatedLoot.set(0, new ItemStack(SimpleMusket.NETHERITE_BULLET.get(), amount));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}