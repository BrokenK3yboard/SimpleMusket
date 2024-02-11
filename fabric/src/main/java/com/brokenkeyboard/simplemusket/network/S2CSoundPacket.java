package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.joml.Vector3f;

public class S2CSoundPacket implements FabricPacket {

    private final SoundEvent SOUND;
    private final SoundSource SOURCE;
    private final Vector3f POS;

    public S2CSoundPacket(FriendlyByteBuf buf) {
        SOUND = SoundEvent.readFromNetwork(buf);
        SOURCE = buf.readEnum(SoundSource.class);
        POS = buf.readVector3f();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        SOUND.writeToNetwork(buf);
        buf.writeEnum(SOURCE);
        buf.writeVector3f(POS);
    }

    @Override
    public PacketType<?> getType() {
        return SimpleMusket.SOUND_TYPE;
    }
}