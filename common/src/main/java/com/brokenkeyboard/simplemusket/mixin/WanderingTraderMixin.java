package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.entity.Gunslinger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    protected WanderingTraderMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    public void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Gunslinger.class, 32.0F, 0.5, 0.5));
    }
}