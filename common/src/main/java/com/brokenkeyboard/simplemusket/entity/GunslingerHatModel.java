package com.brokenkeyboard.simplemusket.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class GunslingerHatModel<T extends HumanoidRenderState> extends HumanoidModel<T> {

    public GunslingerHatModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new CubeDeformation(0.51f)), PartPose.ZERO);
        head.addOrReplaceChild("hat_rim", CubeListBuilder.create().texOffs(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), PartPose.rotation(-1.5707964f, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}