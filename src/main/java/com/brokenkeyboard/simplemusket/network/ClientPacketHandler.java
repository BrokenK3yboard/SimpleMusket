package com.brokenkeyboard.simplemusket.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandler {

    public static void playSound(SoundEvent sound, SoundSource source, BlockPos pos) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            double distance = Math.sqrt(player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
            float volume = (float) Math.max(1F - (distance / 32), 0.15F);
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, source, volume, 0.8F, true);
        }
    }
}