package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

public class NeoPacketHandler {

    public static void handleData(final S2CSoundPayload data, final IPayloadContext context) {
        boolean playerSource = data.playerSource();
        Vector3f pos = data.pos();
        context.enqueueWork(() -> ClientPacketHandler.playSound(playerSource, pos));
    }
}