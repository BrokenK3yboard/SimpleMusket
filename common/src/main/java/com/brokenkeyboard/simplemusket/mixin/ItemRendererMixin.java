package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static final ModelResourceLocation MUSKET_MODEL = ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_inventory"));

    @Shadow @Final private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel guiModel(BakedModel defaultModel, ItemStack stack, ItemDisplayContext context) {
        if (context == ItemDisplayContext.GUI && stack.is(ModRegistry.MUSKET))
            return this.itemModelShaper.getModelManager().getModel(MUSKET_MODEL);
        return defaultModel;
    }
}