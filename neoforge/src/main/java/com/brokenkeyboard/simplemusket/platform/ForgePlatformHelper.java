package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.network.Network;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.entity.PartEntity;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void playSound(SoundEvent sound, SoundSource source, ServerLevel level, Vec3 origin) {
        Network.S2CSound(sound, source, level.dimension(), origin);
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof PartEntity<?> part ? part.getParent() : entity;
    }
}