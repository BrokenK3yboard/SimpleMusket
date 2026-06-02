package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class SkeletonLoot extends LootModifier {

    public static final Supplier<MapCodec<SkeletonLoot>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, SkeletonLoot::new)));

    protected SkeletonLoot(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        if (lootContext.getParam(LootContextParams.THIS_ENTITY) instanceof AbstractSkeleton skeleton && skeleton.isHolding(stack -> stack.getItem() instanceof MusketItem)) {
            ObjectArrayList<ItemStack> newDrops = new ObjectArrayList<>();

            for (ItemStack stack1 : generatedLoot) {
                newDrops.add(stack1.is(Items.ARROW) ? new ItemStack(ModRegistry.CARTRIDGE, stack1.getCount()) : stack1);
            }
            return newDrops;
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
