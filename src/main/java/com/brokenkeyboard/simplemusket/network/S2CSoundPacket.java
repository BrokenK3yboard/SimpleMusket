package com.brokenkeyboard.simplemusket.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import javax.lang.model.util.Elements;
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

    public static void handleS2CSound(S2CSoundPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        Vec3 origin = new Vec3(packet.ORIGIN.x(), packet.ORIGIN.y(), packet.ORIGIN.z());
        SoundEvent sound = packet.SOUND;
        SoundSource source = packet.SOURCE;
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.playSound(origin, sound, source)));
        supplier.get().setPacketHandled(true);
    }
}