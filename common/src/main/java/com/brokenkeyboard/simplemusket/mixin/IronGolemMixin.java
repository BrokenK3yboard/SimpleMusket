package com.brokenkeyboard.simplemusket.mixin;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends EntityDeflectMixin {

    @Unique
    private static final ProjectileDeflection BULLET_DEFLECTION = (projectile, entity, randomSource) -> {
        assert entity != null;
        entity.level().playSound(null, entity, SoundEvents.IRON_GOLEM_HURT, entity.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(projectile, entity, randomSource);
    };

    @Override
    public void deflect(Projectile projectile, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (projectile instanceof BulletEntity bullet) {
            ((IronGolem) (Object) this).hurt(bullet.damageSource(bullet, projectile.getOwner()), bullet.getDamage() * 0.25F);
            cir.setReturnValue(BULLET_DEFLECTION);
        }
    }
}
