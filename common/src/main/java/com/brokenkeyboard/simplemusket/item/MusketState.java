package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.entity.Gunslinger;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record MusketState() implements SelectItemModelProperty<MusketItem.UseState> {

    private static final Codec<MusketItem.UseState> CODEC = MusketItem.UseState.CODEC;
    public static final SelectItemModelProperty.Type<MusketState, MusketItem.UseState> TYPE = Type.create(MapCodec.unit(new MusketState()), CODEC);

    @Override
    public MusketItem.UseState get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        if (livingEntity != null) {
            if (livingEntity instanceof Gunslinger gunslinger && gunslinger.isUsingSawnOff()) {
                return MusketItem.UseState.SAWNOFF;
            } else if (livingEntity instanceof Mob mob && Services.PLATFORM.isMobUsingBayonet(mob)) {
                return MusketItem.UseState.BAYONET;
            } else if (ItemStack.isSameItem(itemStack, livingEntity.getUseItem())) {
                return MusketItem.isLoaded(itemStack) ? MusketItem.UseState.AIMING : MusketItem.UseState.LOADING;
            }
        }
        return MusketItem.isLoaded(itemStack) ? MusketItem.UseState.LOADED : MusketItem.UseState.NONE;
    }

    @Override
    public Codec<MusketItem.UseState> valueCodec() {
        return CODEC;
    }

    @Override
    public Type<? extends SelectItemModelProperty<MusketItem.UseState>, MusketItem.UseState> type() {
        return TYPE;
    }
}
