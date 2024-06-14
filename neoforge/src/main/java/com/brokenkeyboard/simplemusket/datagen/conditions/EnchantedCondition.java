package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public class EnchantedCondition implements ICondition {

    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_enchanted");
    public static final ConditionSerializer<EnchantedCondition> SERIALIZER = new ConditionSerializer<>(NAME, EnchantedCondition::new);

    public EnchantedCondition() {
    }

    @Override
    public boolean test(IContext context) {
        return Services.PLATFORM.isModLoaded("consecration");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return null;
    }
}