package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class ClientPacketHandler {

    public static void playSound(SoundEvent sound, SoundSource source, Vector3f pos) {
        Minecraft instance = Minecraft.getInstance();

        if (instance.level != null && instance.player != null) {
            Player player = instance.player;
            double distance = Math.sqrt(player.distanceToSqr(pos.x(), pos.y(), pos.z()));
            float volume = (float) Math.max(1F - (distance / 64), 0.05F);
            instance.level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, source, volume, 0.8F, true);
        }
    }

    public static void setUsingBayonet(int entityID, boolean isUsingBayonet) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.level != null && instance.level.getEntity(entityID) instanceof Mob mob) {
            Services.PLATFORM.setMobUsingBayonet(mob, isUsingBayonet);
        }
    }
}