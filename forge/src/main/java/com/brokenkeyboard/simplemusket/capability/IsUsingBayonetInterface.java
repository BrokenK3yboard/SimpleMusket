package com.brokenkeyboard.simplemusket.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IsUsingBayonetInterface extends INBTSerializable<CompoundTag> {

    void setUsingBayonet(boolean isUsingBayonet);
    boolean getUsingBayonet();
}
