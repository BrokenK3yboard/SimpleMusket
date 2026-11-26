package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;

public class GunslingerRenderer extends IllagerRenderer<Gunslinger, IllagerRenderState> {

    private static final Identifier GUNSLINGER_TEXTURE = ModRegistry.location("textures/entity/gunslinger.png");

    public GunslingerRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
        this.addLayer(new GunslingerHatLayer<>(this));
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public IllagerRenderState createRenderState() {
        return new IllagerRenderState();
    }

    @Override
    public void extractRenderState(Gunslinger entity, IllagerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public Identifier getTextureLocation(IllagerRenderState illagerRenderState) {
        return GUNSLINGER_TEXTURE;
    }
}