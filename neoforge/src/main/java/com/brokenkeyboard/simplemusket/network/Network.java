package com.brokenkeyboard.simplemusket.network;

import com.brokenkeyboard.simplemusket.Constants;
import io.netty.channel.pool.SimpleChannelPool;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public class Network {

    private static SimpleChannel instance;
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    }

    public static void register() {

        SimpleChannelPool network = NetworkRegistry.ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1")
                .clientAcceptedVersions(string -> true)
                .serverAcceptedVersions(string -> true)
                .simpleChannel();

        instance = network;

        network.messageBuilder(S2CSoundPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CSoundPacket::new)
                .encoder(S2CSoundPacket::toBytes)
                .consumerMainThread(S2CSoundPacket::handleS2CSound)
                .add();
    }

    public static void S2CSound(SoundEvent sound, SoundSource source, ResourceKey<Level> dimension, Vec3 origin) {
        instance.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(origin.x, origin.y, origin.z, 128, dimension)),
                new S2CSoundPacket(sound, source, origin.toVector3f()));
    }
}