package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
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

    private int ticksAlive = 0;
    private ItemStack bullet = new ItemStack(ModRegistry.CARTRIDGE);
    @Nullable
    private Vec3 initialPos;
    @Nullable
    private ItemStack weapon;

    private static final ProjectileDeflection BULLET_DEFLECTION = (projectile, entity, randomSource) -> {
        assert entity != null;
        entity.level().playSound(null, entity, SoundEvents.IRON_GOLEM_HURT, entity.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(projectile, entity, randomSource);
    };

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public BulletEntity(Level level, @Nullable Entity owner, Vec3 pos, ItemStack bullet, @Nullable ItemStack weapon) {
        this(ModRegistry.BULLET_ENTITY, level);
        this.setOwner(owner);
        this.setPos(pos);
        this.initialPos = pos;
        this.bullet = bullet;
        this.weapon = weapon != null && level instanceof ServerLevel ? weapon.copy() : null;
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
    protected ProjectileDeflection hitTargetOrDeflectSelf(HitResult result) {
        if (result instanceof EntityHitResult entity && entity.getEntity() instanceof IronGolem golem) {
            golem.hurt(damageSource(this, getOwner()), getDamage() * 0.25F);
            if (golem != this.lastDeflectedBy && this.deflect(BULLET_DEFLECTION, golem, this.getOwner(), false)) {
                this.lastDeflectedBy = golem;
            }
            return BULLET_DEFLECTION;
        }
        return super.hitTargetOrDeflectSelf(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity target = Services.PLATFORM.getHitEntity(entity);
        Entity owner = this.getOwner();
        boolean raiderFF = (owner instanceof Raider && target instanceof Raider) && (this.lastDeflectedBy == null);
        boolean playerFF = owner instanceof Player player && target instanceof Player player1 && !player.canHarmPlayer(player1);
        if (raiderFF || playerFF) return;
        float damage = getDamage();

        if (weapon != null && initialPos != null) {
            int longshot = EnchantmentHelper.getItemEnchantmentLevel(level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModRegistry.LONGSHOT), weapon);
            double distance = Math.min(initialPos.distanceTo(entity.position()), 48);
            if (longshot > 0 && distance >= 16) {
                int longshotLevel = target instanceof LivingEntity living && living.hasEffect(ModRegistry.HEX_EFFECT) ? longshot + 2 : longshot;
                double coefficient = ((0.029296875 * distance * distance) - (0.46875 * distance) + 15) / 3 * (3 + ((longshotLevel - 1) * 2)) / 100;
                damage *= (float) (1 + coefficient);
            }
        }

        if (entity.hurt(damageSource(this, owner), damage) && entity instanceof LivingEntity living) {
            if (bullet.is(ModRegistry.HELLFIRE_CARTRIDGE)) {
                living.addEffect(new MobEffectInstance(ModRegistry.ARMOR_DECREASE_EFFECT, 600));
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

    public DamageSource damageSource(BulletEntity bullet, @Nullable Entity attacker) {
        return level().damageSources().source(ModRegistry.BULLET, bullet, attacker);
    }

    public ItemStack getBullet() {
        return bullet;
    }

    public float getDamage() {
        float damage = bullet.getItem() == ModRegistry.HELLFIRE_CARTRIDGE ? 20F : 16F;
        return this.getOwner() instanceof Mob && Config.REDUCE_MOB_DAMAGE.get() ? (float) (damage * 0.75) : damage;
    }
}