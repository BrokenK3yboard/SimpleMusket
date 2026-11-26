package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record MusketLoading() implements RangeSelectItemModelProperty {

    public static final MapCodec<MusketLoading> MAP_CODEC = MapCodec.unit(new MusketLoading());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        LivingEntity entity = itemOwner == null ? null : itemOwner.asLivingEntity();
        if (entity == null) {
            return 0.0F;
        } else if (MusketItem.isLoaded(itemStack)) {
            return 0.0F;
        } else {
            return (float) (itemStack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / Config.RELOAD_TIME.get();
        }
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
