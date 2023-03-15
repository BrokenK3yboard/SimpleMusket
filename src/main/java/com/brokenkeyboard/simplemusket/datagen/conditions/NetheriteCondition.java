package com.brokenkeyboard.simplemusket.datagen.conditions;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class NetheriteCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(SimpleMusket.MOD_ID, "netherite_enabled");
    public static final ConditionSerializer<NetheriteCondition> SERIALIZER = new ConditionSerializer<>(NAME, NetheriteCondition::new);

    public NetheriteCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Config.CRAFT_NETHERITE_BULLETS.get();
    }
}
