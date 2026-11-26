package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;

public class GunslingerHatLayer<S extends IllagerRenderState, M extends IllagerModel<? super S>> extends RenderLayer<S, M> {

    public static final Identifier GUNSLINGER_HAT_TEXTURE = ModRegistry.location("textures/entity/gunslinger_hat.png");
    public static final ModelLayerLocation GUNSLINGER_HAT = new ModelLayerLocation(ModRegistry.location("gunslinger"), "gunslinger_hat");
    private final EntityModel<S> gunslingerHatModel;

    public GunslingerHatLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
        gunslingerHatModel = new GunslingerHatModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(GUNSLINGER_HAT));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S renderState, float yRot, float xRot) {
        if (renderState.isInvisible) return;
        poseStack.pushPose();
        this.getParentModel().getHat().translateAndRotate(poseStack);
        renderColoredCutoutModel(gunslingerHatModel, GUNSLINGER_HAT_TEXTURE, poseStack, submitNodeCollector, lightCoords, renderState, -1, 0);
        poseStack.popPose();
    }
}