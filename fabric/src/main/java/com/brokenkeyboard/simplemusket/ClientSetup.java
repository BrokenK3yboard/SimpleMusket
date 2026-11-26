package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.GunslingerHatLayer;
import com.brokenkeyboard.simplemusket.entity.GunslingerHatModel;
import com.brokenkeyboard.simplemusket.entity.GunslingerRenderer;
import com.brokenkeyboard.simplemusket.item.MusketLoading;
import com.brokenkeyboard.simplemusket.item.MusketState;
import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(ModRegistry.BULLET_ENTITY.value(), BulletEntityRenderer::new);
        EntityRenderers.register(ModRegistry.GUNSLINGER.value(), GunslingerRenderer::new);
        ModelLayerRegistry.registerModelLayer(GunslingerHatLayer.GUNSLINGER_HAT, GunslingerHatModel::createBodyLayer);

        SelectItemModelProperties.ID_MAPPER.put(ModRegistry.location("musket_state"), MusketState.TYPE);
        RangeSelectItemModelProperties.ID_MAPPER.put(ModRegistry.location("musket_loading"), MusketLoading.MAP_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(S2CSoundPayload.TYPE, (payload, context) ->
                context.client().execute(() -> ClientPacketHandler.playSound(payload.soundSource(), payload.pos())));
    }
}