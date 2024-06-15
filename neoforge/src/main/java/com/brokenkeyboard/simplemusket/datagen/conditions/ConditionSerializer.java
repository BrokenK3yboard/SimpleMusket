package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.function.Supplier;

public record ConditionSerializer<T extends ICondition>(ResourceLocation id, Supplier<T> supplier) implements ICondition {

    @Override
    public boolean test(IContext context) {
        return false;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return null;
    }
}
