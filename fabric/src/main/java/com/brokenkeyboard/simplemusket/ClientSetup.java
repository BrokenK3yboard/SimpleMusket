package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.HatModel;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModRegistry.BULLET_ENTITY, BulletEntityRenderer::new);
        EntityRendererRegistry.register(ModRegistry.GUNSLINGER, MusketPillagerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(new ModelLayerLocation(ModRegistry.location("musket_pillager"), "overlay"), HatModel::createBodyLayer);
        ModRegistry.registerItemProperties();

        PayloadTypeRegistry.playC2S().register(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(S2CSoundPayload.TYPE, (payload, context) ->
                context.client().execute(() -> ClientPacketHandler.playSound(payload.soundSource(), payload.pos())));
    }
}