package com.brokenkeyboard.simplemusket;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class SMModelPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context context) {
        context.addModels(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "musket_inventory"));
    }
}