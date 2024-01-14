package com.brokenkeyboard.simplemusket.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class S2CSoundPacket {

    private final SoundEvent SOUND;
    private final SoundSource SOURCE;
    private final Vector3f ORIGIN;

    public S2CSoundPacket(SoundEvent sound, SoundSource source, Vector3f origin) {
        this.SOUND = sound;
        this.SOURCE = source;
        this.ORIGIN = origin;
    }

    public S2CSoundPacket(FriendlyByteBuf buf) {
        SOUND = SoundEvent.readFromNetwork(buf);
        SOURCE = buf.readEnum(SoundSource.class);
        ORIGIN = buf.readVector3f();
    }

    public void toBytes(FriendlyByteBuf buf) {
        SOUND.writeToNetwork(buf);
        buf.writeEnum(SOURCE);
        buf.writeVector3f(ORIGIN);
    }

    public static void handle(S2CSoundPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
                Player player = Minecraft.getInstance().player;
                double distance = Math.sqrt(player.distanceToSqr(packet.ORIGIN.x(), packet.ORIGIN.y(), packet.ORIGIN.z()));
                float volume = (float) Math.max(1F - (distance / 32), 0.15F);
                Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), packet.SOUND, packet.SOURCE, volume, 0.8F, true);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
