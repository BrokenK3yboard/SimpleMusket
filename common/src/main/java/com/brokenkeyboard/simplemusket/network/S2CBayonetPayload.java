package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record S2CBayonetPayload(int entityID, boolean isUsingBayonet) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CBayonetPayload> TYPE = new CustomPacketPayload.Type<>(ModRegistry.location(Constants.IS_USING_BAYONET));

    public static final StreamCodec<ByteBuf, S2CBayonetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, S2CBayonetPayload::entityID,
            ByteBufCodecs.BOOL, S2CBayonetPayload::isUsingBayonet,
            S2CBayonetPayload::new
    );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
