package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.function.Supplier;

public record ConditionSerializer<T extends ICondition>(ResourceLocation id, Supplier<T> supplier) implements IConditionSerializer<T> {

    @Override
    public void write(JsonObject json, T value) {
    }

    @Override
    public T read(JsonObject json) {
        return supplier.get();
    }

    @Override
    public ResourceLocation getID() {
        return id;
    }
}
