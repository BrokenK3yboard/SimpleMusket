package com.brokenkeyboard.simplemusket.network;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {

    public static void playSound(Vec3 origin, SoundEvent sound, SoundSource source) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            double distance = Math.sqrt(player.distanceToSqr(origin.x(), origin.y(), origin.z()));
            float volume = (float) Math.max(1F - (distance / 32), 0.15F);
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, source, volume, 0.8F, true);
        }
    }
}