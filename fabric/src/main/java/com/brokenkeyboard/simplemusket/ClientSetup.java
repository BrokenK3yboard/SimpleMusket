package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.HatModel;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.joml.Vector3f;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModRegistry.BULLET_ENTITY, BulletEntityRenderer::new);
        EntityRendererRegistry.register(ModRegistry.GUNSLINGER, MusketPillagerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "musket_pillager"), "overlay"), HatModel::createBodyLayer);
        ModelLoadingPlugin.register(new SMModelPlugin());
        ModRegistry.registerItemProperties();

        ClientPlayNetworking.registerGlobalReceiver(SimpleMusket.SOUND_TYPE.getId(), (client, handler, buf, responseSender) -> {
                SoundEvent sound = SoundEvent.readFromNetwork(buf);
                SoundSource source = buf.readEnum(SoundSource.class);
                Vector3f location = buf.readVector3f();
                client.execute(() -> ClientPacketHandler.playSound(sound, source, location)
        );});
    }
}