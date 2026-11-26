package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class SMSkeletonLootModifier extends LootModifier {

    public static final Supplier<MapCodec<SMSkeletonLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, SMSkeletonLootModifier::new)));

    protected SMSkeletonLootModifier(LootItemCondition[] conditions, int priority) {
        super(conditions, priority);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        if (lootContext.getParameter(LootContextParams.THIS_ENTITY) instanceof AbstractSkeleton skeleton && skeleton.isHolding(stack -> stack.getItem() instanceof MusketItem)) {
            ObjectArrayList<ItemStack> newDrops = new ObjectArrayList<>();

            for (ItemStack stack1 : generatedLoot) {
                newDrops.add(stack1.is(Items.ARROW) ? new ItemStack(ModRegistry.CARTRIDGE.value(), stack1.getCount()) : stack1);
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
