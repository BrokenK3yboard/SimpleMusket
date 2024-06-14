package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;

public class BulletEntity extends Projectile {

    private Vec3 initialPos;
    private double damage = 16.0F;
    private double pierce = 0;
    private int longshot = 0;
    private int blast = 0;
    private int lifespan = 40;
    private int ticksAlive = 0;
    private double deviation = 12F;
    private boolean isHoly = false;
    private boolean isHellfire = false;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
    }

    public BulletEntity(Level level, Entity owner, Vec3 pos, double damage, double pierce, int longshot, int blast, int lifespan, double deviation, boolean isHoly, boolean isHellfire) {
        super(ModRegistry.BULLET_ENTITY, level);
        this.setOwner(owner);
        this.initialPos = pos;
        this.damage = damage;
        this.pierce = pierce;
        this.longshot = longshot;
        this.blast = blast;
        this.lifespan = lifespan;
        this.deviation = deviation;
        this.setNoGravity(true);
        this.isHoly = isHoly;
        this.isHellfire = isHellfire;
    }

    public void tick() {
        super.tick();
        if (ticksAlive > lifespan) this.discard();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
            this.hasImpulse = true;
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
        ticksAlive++;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity target = Services.PLATFORM.getHitEntity(entity);
        if (this.getOwner() instanceof Raider && target instanceof Raider) return;

        if (longshot > 0) {
            double distance = Math.min(initialPos.distanceTo(entity.position()), 48);
            if (distance >= 16) {
                double coefficient = ((0.029296875 * distance * distance) - (0.46875 * distance) + 15) / 3 * (3 + ((longshot - 1) * 2)) / 100;
                damage *= (1 + coefficient);
            }
        }

        if (target instanceof LivingEntity livingEntity) {
            if (isHoly && livingEntity.getType() == MobType.UNDEAD) damage *= 2;
            if (blast > 0 && initialPos.distanceTo(livingEntity.position()) <= 8) damage *= 1.25;

            double armor = livingEntity.getAttributes().hasAttribute(Attributes.ARMOR) ? Objects.requireNonNull(livingEntity.getAttribute(Attributes.ARMOR)).getValue() : 0;

            if (armor > 0) {
                double toughness = livingEntity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? Objects.requireNonNull(livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS)).getValue() : 0;
                double finalArmor = armor * (1 - pierce);
                damage = (damage * (1 - (Math.min(20, Math.max((finalArmor / 5), finalArmor - ((4 * damage) / (toughness + 8))))) / 25));
            }
        }

        if (entity.hurt(bullet(this, this.getOwner()), (float) damage) && entity instanceof LivingEntity livingEntity && isHellfire) {
            livingEntity.addEffect(new MobEffectInstance(ModRegistry.ARMOR_DECREASE, 300));
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    protected DamageSource bullet(BulletEntity bullet, @Nullable Entity attacker) {
        return level().damageSources().source(Constants.BULLET, bullet, attacker);
    }

    public void setDamageScaling(double multiplier) {
        damage *= multiplier;
    }

    public float getDeviation() {
        return (float) deviation;
    }

    public boolean isHoly() {
        return isHoly;
    }
}