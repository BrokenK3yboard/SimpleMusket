package com.brokenkeyboard.simplemusket.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class S2CSoundPacket {

    private final SoundEvent SOUND;
    private final SoundSource SOURCE;
    private final Vector3f POS;

    public S2CSoundPacket(SoundEvent sound, SoundSource source, Vector3f pos) {
        this.SOUND = sound;
        this.SOURCE = source;
        this.POS = pos;
    }

    public S2CSoundPacket(FriendlyByteBuf buf) {
        SOUND = SoundEvent.readFromNetwork(buf);
        SOURCE = buf.readEnum(SoundSource.class);
        POS = buf.readVector3f();
    }

    public void toBytes(FriendlyByteBuf buf) {
        SOUND.writeToNetwork(buf);
        buf.writeEnum(SOURCE);
        buf.writeVector3f(POS);
    }

    public static void handleS2CSound(S2CSoundPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.playSound(packet.SOUND, packet.SOURCE, packet.POS)));
        supplier.get().setPacketHandled(true);
    }
}