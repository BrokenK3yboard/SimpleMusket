package com.brokenkeyboard.simplemusket.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("CancellableInjectionUsage")
@Mixin(Entity.class)
public abstract class EntityDeflectMixin {

    @Inject(method = "deflection", at = @At("RETURN"), cancellable = true)
    public void deflect(Projectile projectile, CallbackInfoReturnable<ProjectileDeflection> cir) {}
}
