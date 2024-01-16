package com.brokenkeyboard.simplemusket.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSoundPacket {

    private final SoundEvent SOUND;
    private final SoundSource SOURCE;
    private final BlockPos POS;

    public S2CSoundPacket(SoundEvent sound, SoundSource source, BlockPos pos) {
        this.SOUND = sound;
        this.SOURCE = source;
        this.POS = pos;
    }

    public S2CSoundPacket(FriendlyByteBuf buf) {
        SOUND = new SoundEvent(buf.readResourceLocation());
        SOURCE = buf.readEnum(SoundSource.class);
        POS = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(SOUND.getLocation());
        buf.writeEnum(SOURCE);
        buf.writeBlockPos(POS);
    }

    public static void handleS2CSound(S2CSoundPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.playSound(packet.SOUND, packet.SOURCE, packet.POS)));
        supplier.get().setPacketHandled(true);
    }
}