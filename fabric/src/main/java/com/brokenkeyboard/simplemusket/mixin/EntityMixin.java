package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity spawnAtLocation(Level level, double x, double y, double z, ItemStack itemStack, Operation<ItemEntity> original) {
        Entity entity = (Entity) (Object) this;
        boolean modifyDrop = itemStack.is(Items.ARROW) && entity instanceof AbstractSkeleton skeleton && skeleton.isHolding(stack1 -> stack1.getItem() instanceof MusketItem);
        return original.call(level, x, y, z, modifyDrop ? new ItemStack(ModRegistry.CARTRIDGE.value(), itemStack.count()) : itemStack);
    }
}
