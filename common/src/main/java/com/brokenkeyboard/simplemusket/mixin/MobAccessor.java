package com.brokenkeyboard.simplemusket.mixin;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.Mob.class)
public interface MobAccessor {
    @Accessor
    GoalSelector getGoalSelector();
}
