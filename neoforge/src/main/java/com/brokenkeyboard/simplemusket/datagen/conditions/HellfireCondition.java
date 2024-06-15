package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;

public class HellfireCondition implements ICondition {

    public static final HellfireCondition INSTANCE = new HellfireCondition();
    public static final MapCodec<HellfireCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(ICondition.IContext context) {
        return Config.CRAFT_HELLFIRE_CARTRIDGE.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
