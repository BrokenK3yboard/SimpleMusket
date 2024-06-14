package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.Constants;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public class HellfireCondition implements ICondition {

    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "craft_hellfire");
    public static final ConditionSerializer<HellfireCondition> SERIALIZER = new ConditionSerializer<>(NAME, HellfireCondition::new);

    public HellfireCondition() {
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return Config.CRAFT_HELLFIRE_CARTRIDGE.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return null;
    }
}
