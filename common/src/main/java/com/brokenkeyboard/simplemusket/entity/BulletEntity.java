package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.ModEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BulletEntity extends Projectile {

    private static final int MAX_LIFESPAN = 300;
    private int life = 0;
    private float velocityPercentage = 1;
    private boolean isFullyAimed = false;
    @Nullable private ItemStack firedFromWeapon;

    public BulletEntity(EntityType<BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public BulletEntity(Level level, @Nullable Entity owner, double x, double y, double z, @Nullable ItemStack weapon) {
        this(ModRegistry.BULLET_ENTITY.value(), level);
        this.setOwner(owner);
        this.setPos(x, y, z);
        this.firedFromWeapon = weapon != null && level instanceof ServerLevel ? weapon.copy() : null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void shoot(double xd, double yd, double zd, float pow, float uncertainty) {
        super.shoot(xd, yd, zd, pow, uncertainty);
        this.life = 0;
        if (this.firedFromWeapon != null && (uncertainty == 0 || this.getOwner() instanceof Mob)) {
            this.isFullyAimed = true;
        }
    }

    @Override
    public void tick() {
        applyInertia();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        boolean hitObject = !this.noPhysics && hitResult.getType() != HitResult.Type.MISS;
        Vec3 newPosition;
        if (hitObject) {
            newPosition = hitResult.getLocation();
            this.needsSync = true;
        } else {
            newPosition = this.position().add(this.getDeltaMovement());
        }

        this.setPos(newPosition);
        this.applyEffectsFromBlocks();
        if (this.isAlive() && hitObject) {
            this.hitTargetOrDeflectSelf(hitResult);
        }

        super.tick();
        if (life++ > MAX_LIFESPAN) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity victim = hitResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = damageSource(this, owner);
        float damage = (float) (this.getOwner() instanceof Mob ? Config.BULLET_DAMAGE.get() * Config.MOB_DAMAGE_MULTIPLIER.get() : Config.BULLET_DAMAGE.get());

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.firedFromWeapon != null) {
                damage = EnchantmentHelper.modifyDamage(serverLevel, this.firedFromWeapon, victim, damageSource, damage);
                damage = ModEnchantments.modifyDamageDistance(this.firedFromWeapon, victim, damageSource, damage);
            }

            if (victim instanceof IronGolem) {
                damage *= 0.5F;
            }

            if (owner instanceof LivingEntity livingOwner) {
                livingOwner.setLastHurtMob(victim);
            }

            if (victim.hurtServer(serverLevel, damageSource, damage * this.velocityPercentage) && victim instanceof LivingEntity living) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, living, damageSource, this.getWeaponItem());
            }
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    @Nullable
    @Override
    public ItemStack getWeaponItem() {
        return firedFromWeapon;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();
        if (owner instanceof Player player && entity instanceof Player target && !player.canHarmPlayer(target)) {
            return false;
        } else if (owner instanceof Raider && entity instanceof Raider && this.lastDeflectedBy == null) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("life", this.life);
        output.putFloat("velocityPercentage", this.velocityPercentage);
        output.putBoolean("isFullyAimed", this.isFullyAimed);
        output.storeNullable("weapon", ItemStack.CODEC, this.firedFromWeapon);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.life = input.getIntOr("life", 0);
        this.velocityPercentage = input.getFloatOr("velocityPercentage", 1);
        this.isFullyAimed = input.getBooleanOr("isFullyAimed", false);
        this.firedFromWeapon = input.read("weapon", ItemStack.CODEC).orElse(null);
    }

    private void applyInertia() {
        if (this.isInWater()) {
            Vec3 movement = this.getDeltaMovement();
            for (int j = 0; j < 4; ++j) {
                this.level().addParticle(ParticleTypes.BUBBLE, getX(), getY(), getZ(), movement.x * -0.25, movement.y * -0.25, movement.z * -0.25);
            }

            this.velocityPercentage *= 0.8F;
            this.setDeltaMovement(movement.scale(0.8F));
            if (velocityPercentage < 0.2F) {
                this.discard();
            } else if (velocityPercentage < 0.5F) {
                this.setNoGravity(false);
            }
        }
    }

    private DamageSource damageSource(BulletEntity bullet, @Nullable Entity attacker) {
        return this.level().damageSources().source(ModRegistry.BULLET, bullet, attacker);
    }

    public boolean isFullyAimed() {
        return isFullyAimed;
    }
}