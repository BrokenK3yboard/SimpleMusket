package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class EnchantedCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Constants.MOD_ID, "craft_enchanted");
    public static final ConditionSerializer<EnchantedCondition> SERIALIZER = new ConditionSerializer<>(NAME, EnchantedCondition::new);

    public EnchantedCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Config.CRAFT_ENCHANTED_CARTRIDGE.get();
    }
}