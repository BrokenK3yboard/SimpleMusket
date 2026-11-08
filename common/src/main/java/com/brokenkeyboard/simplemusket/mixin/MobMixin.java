package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.platform.Services;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {

    @WrapOperation(method = "getAttackBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"))
    private AABB modifyMeleeRange(AABB aabb, double x, double y, double z, Operation<AABB> original) {
        if (Services.PLATFORM.isMobUsingBayonet((Mob) (Object) this)) {
            original.call(aabb, x + 0.6, y, z + 0.6);
        }
        return original.call(aabb, x, y, z);
    }
}
