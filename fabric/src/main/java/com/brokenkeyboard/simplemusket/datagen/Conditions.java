package com.brokenkeyboard.simplemusket.datagen;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;

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

    public static ConditionJsonProvider configBooleans(ResourceLocation id, String... configValues) {
        Preconditions.checkArgument(configValues.length > 0, "Must register at least one config boolean.");

        return new ConditionJsonProvider() {

            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();

                for (String fieldName : configValues) {
                    array.add(fieldName);
                }

                object.add("values", array);
            }
        };
    }
}