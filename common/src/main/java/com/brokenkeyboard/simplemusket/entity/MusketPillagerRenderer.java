package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class MusketPillagerRenderer extends IllagerRenderer<MusketPillager> {
    private static final ResourceLocation PILLAGER = ModRegistry.location("textures/entity/gunslinger.png");

    public MusketPillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new MusketPillagerLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MusketPillager entity) {
        return PILLAGER;
    }
}