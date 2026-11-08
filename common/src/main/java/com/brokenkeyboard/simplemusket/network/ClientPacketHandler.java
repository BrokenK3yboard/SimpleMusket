package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class ClientPacketHandler {

    public static void playSound(String soundSource, Vector3f pos) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            SoundSource source = SoundSource.valueOf(soundSource);
            double distance = Math.sqrt(player.distanceToSqr(pos.x(), pos.y(), pos.z()));
            float volume = (float) Math.max(1F - (distance / 64), 0.05F);
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), ModRegistry.MUSKET_FIRE, source, volume, 0.8F, true);
        }
    }

    public static void setUsingBayonet(int entityID, boolean isUsingBayonet) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.level != null && instance.level.getEntity(entityID) instanceof Mob mob) {
            Services.PLATFORM.setMobUsingBayonet(mob, isUsingBayonet);
        }
    }
}