package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class HellfireCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Constants.MOD_ID, "craft_hellfire");
    public static final ConditionSerializer<HellfireCondition> SERIALIZER = new ConditionSerializer<>(NAME, HellfireCondition::new);

    public HellfireCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Config.CRAFT_HELLFIRE_CARTRIDGE.get();
    }
}
