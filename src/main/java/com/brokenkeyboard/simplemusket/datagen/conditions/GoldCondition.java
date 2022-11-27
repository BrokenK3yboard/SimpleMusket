package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class GoldCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(SimpleMusket.MOD_ID, "gold_enabled");
    public static final ConditionSerializer<GoldCondition> SERIALIZER = new ConditionSerializer<>(NAME, GoldCondition::new);

    public GoldCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Config.CRAFT_GOLD_BULLETS.get();
    }

    @Override
    public boolean test() {
        return false;
    }
}
