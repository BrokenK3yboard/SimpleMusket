package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bullet.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public BulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(BulletEntity bulletEntity, float v, float v1, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        poseStack.pushPose();
        poseStack.scale(0.15F, 0.15F, 0.15F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        vertex(vertexconsumer, matrix4f, pose, i, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, pose, i, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, pose, i, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, pose, i, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(bulletEntity, v, v1, poseStack, bufferSource, i);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose pose, int i, float v, int i1, int i2, int i3) {
        consumer.addVertex(matrix4f, v - 0.5F, (float)i1 - 0.25F, 0.0F).setColor(255, 255, 255, 255)
                .setUv(i2, i3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(i).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(BulletEntity bulletEntity) {
        return TEXTURE;
    }
}