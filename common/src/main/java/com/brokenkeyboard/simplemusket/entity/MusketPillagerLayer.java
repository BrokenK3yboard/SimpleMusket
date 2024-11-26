package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class MusketPillagerLayer extends RenderLayer<MusketPillager, IllagerModel<MusketPillager>> {

    public static final ResourceLocation TEXTURE = ModRegistry.location("textures/entity/gunslinger_layer.png");
    private final HatModel<MusketPillager> MODEL;

    public MusketPillagerLayer(LivingEntityRenderer<MusketPillager, IllagerModel<MusketPillager>> parent) {
        super(parent);
        MODEL = new HatModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(new ModelLayerLocation(ModRegistry.location("musket_pillager"), "overlay")));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, MusketPillager pillager, float v, float v1, float v2, float v3, float v4, float v5) {
        if (pillager.isInvisible()) return;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.getParentModel().getHead().translateAndRotate(poseStack);
        MODEL.renderToBuffer(poseStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(pillager, 0.0F), -1);
    }
}