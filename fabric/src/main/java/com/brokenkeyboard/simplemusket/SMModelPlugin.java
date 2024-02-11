package com.brokenkeyboard.simplemusket;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import static com.brokenkeyboard.simplemusket.Constants.MUSKET_MODEL;

public class SMModelPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context context) {
        context.addModels(MUSKET_MODEL);
    }
}