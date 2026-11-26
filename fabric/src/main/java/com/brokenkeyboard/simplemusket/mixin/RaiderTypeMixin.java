package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Raid.RaiderType.class)
public enum RaiderTypeMixin {
    SIMPLEMUSKET_GUNSLINGER(ModRegistry.GUNSLINGER.value(), Constants.GUNSLINGER_RAID_SPAWN_COUNTS);

    @Shadow
    RaiderTypeMixin(final EntityType<? extends Raider> entityType, final int[] spawnsPerWaveBeforeBonus) {}
}
