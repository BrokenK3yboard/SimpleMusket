package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.phys.Vec3;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public void playSound(SoundSource source, ServerLevel level, Vec3 origin) {
        for (ServerPlayer serverPlayer : PlayerLookup.around(level, origin, 128)) {
            ServerPlayNetworking.send(serverPlayer, new S2CSoundPayload(source.toString(), origin.toVector3f()));
        }
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof EnderDragonPart part ? part.parentMob : entity;
    }

    @Override
    public Holder<MobEffect> createEffectHolder(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), effect);
    }
}