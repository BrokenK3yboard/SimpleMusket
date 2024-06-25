package com.brokenkeyboard.simplemusket.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;

public class HatModel<T extends LivingEntity> extends EntityModel<T> {

    private static final String HAT = "hat";
    private static final String HAT_RIM = "hat_rim";
    private final ModelPart hat;
    private final ModelPart hat_rim;

    public HatModel(ModelPart part) {
        super(RenderType::entityCutoutNoCull);
        this.hat = part.getChild(HAT);
        this.hat_rim = this.hat.getChild(HAT_RIM);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        hat.render(poseStack,vertexConsumer, i, i1, i2);
        hat_rim.render(poseStack,vertexConsumer, i, i1, i2);
    }

    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {}

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition hat = partDefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new CubeDeformation(0.51f)), PartPose.ZERO);
        hat.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), PartPose.rotation(-1.5707964f, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}