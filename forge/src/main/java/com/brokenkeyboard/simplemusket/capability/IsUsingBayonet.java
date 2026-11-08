package com.brokenkeyboard.simplemusket.capability;

import com.brokenkeyboard.simplemusket.Constants;
import net.minecraft.nbt.CompoundTag;

public class IsUsingBayonet implements IsUsingBayonetInterface {

    private boolean isUsingBayonet = false;

    @Override
    public void setUsingBayonet(boolean isUsingBayonet) {
        this.isUsingBayonet =  isUsingBayonet;
    }

    @Override
    public boolean getUsingBayonet() {
        return this.isUsingBayonet;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putBoolean(Constants.IS_USING_BAYONET, isUsingBayonet);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.isUsingBayonet = tag.getBoolean(Constants.IS_USING_BAYONET);
    }
}
