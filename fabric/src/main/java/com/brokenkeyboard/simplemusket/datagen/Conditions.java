package com.brokenkeyboard.simplemusket.datagen;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

public class Conditions {

    public static boolean enabledRecipe(JsonObject object, ForgeConfigSpec.BooleanValue value) {
        JsonArray array = GsonHelper.getAsJsonArray(object, "values");

        for (JsonElement element : array) {
            if (element.isJsonPrimitive()) {
                return value.get();
            }
        }
        return false;
    }

    public static ResourceCondition configBooleans(ResourceLocation id, String... configValues) {
        Preconditions.checkArgument(configValues.length > 0, "Must register at least one config boolean.");

        return new ResourceCondition() {

            @Override
            public ResourceConditionType<?> getType() {
                return null;
            }

            @Override
            public boolean test(@Nullable HolderLookup.Provider registryLookup) {
                return false;
            }
        };
    }
}