package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.ModRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record S2CSoundPayload(String soundSource, Vector3f pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CSoundPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location("sound"));

    public static final StreamCodec<ByteBuf, S2CSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, S2CSoundPayload::soundSource,
            ByteBufCodecs.VECTOR3F, S2CSoundPayload::pos,
            S2CSoundPayload::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}