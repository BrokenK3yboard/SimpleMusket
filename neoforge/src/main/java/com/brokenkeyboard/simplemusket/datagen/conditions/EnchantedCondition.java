package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.mojang.serialization.MapCodec;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class EnchantedCondition implements ICondition {

    @Override
    public boolean test(IContext context) {
        return ModList.get().isLoaded("consecration");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return ModLoadedCondition.CODEC;
    }
}