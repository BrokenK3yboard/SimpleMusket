package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity, EntityRenderState> {

    private static final Identifier TEXTURE = ModRegistry.location("textures/entity/bullet.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(TEXTURE);

    public BulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(0.15F, 0.15F, 0.15F);
        poseStack.mulPose(camera.orientation);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, int lightCoords, float x, int y, int u, int v) {
        builder.addVertex(pose, x - 0.5F, y - 0.25F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}