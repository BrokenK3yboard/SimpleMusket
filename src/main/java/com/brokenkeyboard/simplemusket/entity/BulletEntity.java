package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.brokenkeyboard.simplemusket.SimpleMusket.CONSECRATION;

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

    public BulletEntity(Level level, Entity owner, Vec3 initalPos, float damage, double pierce, int longshot, boolean noIframe, boolean isHoly) {
        super(SimpleMusket.BULLET_ENTITY.get(), level);
        this.setOwner(owner);
        this.initialPos = initalPos;
        this.damage = damage;
        this.pierce = pierce;
        this.longshot = longshot;
        this.noIframe = noIframe;
        this.isHoly = isHoly;
        this.setNoGravity(true);
    }

    public void tick() {
        super.tick();
        if (ticksAlive > 40) this.discard();

        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);

        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
        ticksAlive++;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {

        Entity entity = hitResult.getEntity();
        Entity target = entity instanceof PartEntity<?> part ? part.getParent() : entity;

        if (this.getOwner() instanceof Raider && target instanceof Raider) return;

        if (longshot > 0) {
            double distance = Math.sqrt(entity.distanceToSqr(initialPos));
            double coefficient = Math.min(distance / 50, 1) * 0.25 * longshot;
            damage *= (float) (1 + coefficient);
        }

        if (target instanceof LivingEntity livingEntity) {

            if (CONSECRATION && Config.CONSECRATION_COMPAT.get() && isHoly && livingEntity.getMobType() == MobType.UNDEAD) {
                damage *= 2;
            }

            double armor = livingEntity.getAttributes().hasAttribute(Attributes.ARMOR) ? Objects.requireNonNull(livingEntity.getAttribute(Attributes.ARMOR)).getValue() : 0;
            double toughness = livingEntity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS) ? Objects.requireNonNull(livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS)).getValue() : 0;
            double finalArmor = armor * (1 - pierce);
            damage = (float) (damage * (1 - (Math.min(20, Math.max((finalArmor / 5), finalArmor - ((4 * damage) / (toughness + 8))))) / 25));
        }

        entity.hurt(bullet(this, this.getOwner()), damage);
        if (noIframe) entity.invulnerableTime = 0;
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        this.discard();
    }

    protected void defineSynchedData() {
    }

    protected DamageSource bullet(BulletEntity bullet, @Nullable Entity attacker) {
        return (new IndirectEntityDamageSource("bullet", bullet, attacker)).setProjectile().bypassArmor();
    }

    public void setDamageScaling(double multiplier) {
        damage *= (float) (multiplier * (1 - ((3 - getLevel().getDifficulty().getId()) * 0.25)));
    }

    public boolean isHoly() {
        return isHoly;
    }
}