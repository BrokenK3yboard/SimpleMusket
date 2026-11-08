package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public class S2CBayonetPacket implements FabricPacket {

    private final int entityID;
    private final boolean isUsingBayonet;

    public S2CBayonetPacket(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        isUsingBayonet = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(isUsingBayonet);
    }

    @Override
    public PacketType<?> getType() {
        return SimpleMusket.BAYONET_TYPE;
    }
}
