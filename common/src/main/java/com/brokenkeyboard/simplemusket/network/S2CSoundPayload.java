package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record S2CSoundPayload(boolean playerSource, Vector3f pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CSoundPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sound"));

    public static final StreamCodec<ByteBuf, S2CSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CSoundPayload::playerSource,
            ByteBufCodecs.VECTOR3F, S2CSoundPayload::pos,
            S2CSoundPayload::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}