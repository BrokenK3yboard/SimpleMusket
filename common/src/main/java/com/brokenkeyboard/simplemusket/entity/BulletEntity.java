package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BulletEntity extends Projectile {

    private double damage = 16.0F;
    private int longshot = 0;
    private int ticksAlive = 0;
    private ItemStack bullet = new ItemStack(ModRegistry.CARTRIDGE);
    private boolean deflected = false;
    @Nullable
    private Vec3 initialPos;
    @Nullable
    private ItemStack weapon;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public BulletEntity(Level level, Entity owner, Vec3 pos, double damage, ItemStack bullet, @Nullable ItemStack weapon) {
        this(ModRegistry.BULLET_ENTITY, level);
        this.setOwner(owner);
        this.initialPos = pos;
        this.damage = damage;
        this.bullet = bullet;

        if (weapon != null && level instanceof ServerLevel) {
            this.longshot = EnchantmentHelper.getItemEnchantmentLevel(level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.LONGSHOT), weapon);
            this.weapon = weapon.copy();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksAlive > 300) this.discard();
        ticksAlive++;
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitresult.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf(hitresult);
            this.hasImpulse = true;
        }

        Vec3 vec3 = this.getDeltaMovement();
        this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity target = Services.PLATFORM.getHitEntity(entity);
        Entity owner = this.getOwner();
        boolean checkFF = (owner instanceof Raider && target instanceof Raider) || (owner instanceof Player player && target instanceof Player player1 && !player.canHarmPlayer(player1));
        if (!deflected && checkFF) return;

        if (longshot > 0 && initialPos != null) {
            double distance = Math.min(initialPos.distanceTo(entity.position()), 48);
            if (distance >= 16) {
                int longshotLevel = entity instanceof LivingEntity living && living.hasEffect(ModRegistry.HEX_EFFECT) ? longshot + 2 : longshot;
                double coefficient = ((0.029296875 * distance * distance) - (0.46875 * distance) + 15) / 3 * (3 + ((longshotLevel - 1) * 2)) / 100;
                damage *= (1 + coefficient);
            }
        }

        if (entity.hurt(bullet(this, this.getOwner()), (float) damage) && entity instanceof LivingEntity living) {
            if (bullet.is(ModRegistry.HELLFIRE_CARTRIDGE)) {
                living.addEffect(new MobEffectInstance(ModRegistry.HELLFIRE_EFFECT, 600));
            } else if (bullet.is(ModRegistry.ENCHANTED_CARTRIDGE)) {
                living.addEffect(new MobEffectInstance(ModRegistry.HEX_EFFECT, 600));
            }
        }
        this.discard();
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.weapon;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    public DamageSource bullet(BulletEntity bullet, @Nullable Entity attacker) {
        return level().damageSources().source(ModRegistry.BULLET, bullet, attacker);
    }

    public void setDeflected() {
        deflected = true;
    }

    public double getDamage() {
        return damage;
    }

    public ItemStack getBullet() {
        return bullet;
    }
}