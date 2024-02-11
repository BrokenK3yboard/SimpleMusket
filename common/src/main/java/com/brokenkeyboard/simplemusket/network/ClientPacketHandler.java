package com.brokenkeyboard.simplemusket.network;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class ClientPacketHandler {

    public static void playSound(SoundEvent sound, SoundSource source, Vector3f pos) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            double distance = Math.sqrt(player.distanceToSqr(pos.x(), pos.y(), pos.z()));
            float volume = (float) Math.max(1F - (distance / 64), 0.05F);
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, source, volume, 0.8F, true);
        }
    }
}