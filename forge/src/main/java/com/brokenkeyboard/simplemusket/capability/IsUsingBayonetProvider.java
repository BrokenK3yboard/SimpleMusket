package com.brokenkeyboard.simplemusket.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IsUsingBayonetProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<IsUsingBayonetInterface> IS_USING_BAYONET_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final IsUsingBayonetInterface backend = new IsUsingBayonet();
    private final LazyOptional<IsUsingBayonetInterface> optionalData = LazyOptional.of(() -> backend);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return IS_USING_BAYONET_CAPABILITY.orEmpty(capability, this.optionalData);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.backend.deserializeNBT(tag);
    }
}
