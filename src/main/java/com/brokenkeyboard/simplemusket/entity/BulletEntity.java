package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.brokenkeyboard.simplemusket.SimpleMusket.BULLET;

public class BulletEntity extends Projectile {

    private Vec3 initialPos;
    private float damage = 16.0F;
    private double pierce = 0;
    private int longshot = 0;
    private int ticksAlive = 0;
    private boolean noIframe;
    private boolean isHoly;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
    }

    public BulletEntity(Level level, Vec3 initalPos, float damage, double pierce, int longshotLevel, boolean noIframe, boolean isHoly) {
        super(SimpleMusket.BULLET_ENTITY.get(), level);
        this.initialPos = initalPos;
        this.damage = damage;
        this.pierce = pierce;
        this.longshot = longshotLevel;
        this.noIframe = noIframe;
        this.isHoly = isHoly;
        this.setNoGravity(true);
    }

    public void tick() {
        super.tick();

        if (ticksAlive > 40) this.discard();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;

        this.level().addParticle(this.getParticle(), this.getX(), this.getY(), this.getZ(), -d1, -d2, -d0);
        this.setPos(d2, d0, d1);
        ticksAlive++;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;

        if (ModList.get().isLoaded("consecration") && Config.CONSECRATION_COMPAT.get() && isHoly && target.getMobType() == MobType.UNDEAD) {
            damage += 7.0F;
        }

        if (longshot > 0) {
            double distance = Math.sqrt(target.distanceToSqr(initialPos));
            double coefficient = (Math.min((distance / 50) * (0.25 * longshot), (0.25 * longshot)));
            damage *= (float) (1 + coefficient);
        }

        double armor = (target.getAttributes().hasAttribute(Attributes.ARMOR) ? Objects.requireNonNull(target.getAttribute(Attributes.ARMOR)).getValue() : 0);
        double toughness = (target.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? Objects.requireNonNull(target.getAttribute(Attributes.ARMOR_TOUGHNESS)).getValue() : 0);
        double finalArmor = armor * (1 - pierce);
        float finalDamage = (float) (damage * (1 - (Math.min(20, Math.max((finalArmor / 5), finalArmor - ((4 * damage) / (toughness + 8))))) / 25));

        DamageSource bulletDamage = (causeBulletDamage(this, this.getOwner()));
        target.hurt(bulletDamage, finalDamage);
        if (noIframe) target.invulnerableTime = 0;

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        this.discard();
    }

    protected void defineSynchedData() {
    }

    protected DamageSource causeBulletDamage(BulletEntity bullet, @Nullable Entity attacker) {
        return (level().damageSources().source(BULLET, bullet, attacker));
    }

    private SimpleParticleType getParticle() {
        return this.isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.SMOKE;
    }

    public void setDamageScaling(double multiplier) {
        damage *= (float) (multiplier * (1 - ((3 - this.level().getDifficulty().getId()) * 0.25)));
    }

    public boolean isHoly() {
        return isHoly;
    }
}