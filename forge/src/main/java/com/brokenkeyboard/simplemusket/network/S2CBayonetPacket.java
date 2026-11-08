package com.brokenkeyboard.simplemusket.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CBayonetPacket {

    private final int entityID;
    private final boolean isUsingBayonet;

    public S2CBayonetPacket(int entityID, boolean isUsingBayonet) {
        this.entityID = entityID;
        this.isUsingBayonet = isUsingBayonet;
    }

    public S2CBayonetPacket(FriendlyByteBuf friendlyByteBuf) {
        this.entityID = friendlyByteBuf.readInt();
        this.isUsingBayonet = friendlyByteBuf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(isUsingBayonet);
    }

    public static void handleS2CBayonet(S2CBayonetPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientPacketHandler.setUsingBayonet(packet.entityID, packet.isUsingBayonet));
        supplier.get().setPacketHandled(true);
    }
}
