package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyReturnValue(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private ItemEntity spawnAtLocation(ItemEntity original) {
        Entity entity = (Entity) (Object) this;
        if (original.getItem().is(Items.ARROW) && entity instanceof AbstractSkeleton skeleton && skeleton.isHolding(stack1 -> stack1.getItem() instanceof MusketItem)) {
            original.setItem(new ItemStack(ModRegistry.CARTRIDGE, original.getItem().getCount()));
        }
        return original;
    }
}
