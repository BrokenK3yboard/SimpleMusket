package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CopperCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(SimpleMusket.MOD_ID, "copper_enabled");
    public static final ConditionSerializer<CopperCondition> SERIALIZER = new ConditionSerializer<>(NAME, CopperCondition::new);

    public CopperCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Config.CRAFT_COPPER_BULLETS.get();
    }
}
