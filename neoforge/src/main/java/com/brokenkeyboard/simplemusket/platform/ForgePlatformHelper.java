package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.brokenkeyboard.simplemusket.SimpleMusket.EFFECTS;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void playSound(SoundSource source, ServerLevel level, Vec3 origin) {
        boolean isPlayerSource = source.equals(SoundSource.PLAYERS);
        PacketDistributor.sendToPlayersNear(level, null, origin.x, origin.y, origin.z, 128, new S2CSoundPayload(isPlayerSource, origin.toVector3f()));
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof PartEntity<?> part ? part.getParent() : entity;
    }

    @Override
    public Holder<MobEffect> createArmorDecreaseEffect() {
        return EFFECTS.register("armor_decrease", () -> ModRegistry.ARMOR_DECREASE);
    }

    @Override
    public Holder<MobEffect> createHexEffect() {
        return EFFECTS.register("hex", () -> ModRegistry.HEX);
    }
}