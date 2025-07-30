package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.mixin.DamageSourcesInvoker;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
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
    private BulletItem bullet = (BulletItem) ModRegistry.CARTRIDGE;
    private float damage = Config.BULLET_DAMAGE.get().floatValue();
    private float velocityPerc = 1F;
    @Nullable
    private ItemStack weapon;
    private boolean speedUpdated = false;

    public BulletEntity(EntityType<? extends BulletEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public BulletEntity(Level level, @Nullable Entity owner, Vec3 pos, ItemStack bullet, @Nullable ItemStack weapon) {
        this(ModRegistry.BULLET_ENTITY, level);
        this.setOwner(owner);
        this.setPos(pos);
        if (MusketItem.BULLETS.test(bullet)) this.bullet = (BulletItem) bullet.getItem();
        if (this.bullet.equals(ModRegistry.HELLFIRE_CARTRIDGE)) damage *= 1.25F;
        if (owner instanceof MusketPillager mob && mob.isUsingSawnOff()) damage *= 0.6F;
        this.weapon = weapon != null && level instanceof ServerLevel ? weapon : null;
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksAlive > 300) this.discard();
        ticksAlive++;
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        Vec3 movement = getDeltaMovement();
        float inertia = 1F;

        if (hitresult.getType() != HitResult.Type.MISS && !Services.PLATFORM.bulletHitResult(this, hitresult)) {
            this.onHit(hitresult);
            this.hasImpulse = true;
        }

        if (isInWater()) {
            inertia = 0.8F;
            velocityPerc *= 0.8F;
            for(int j = 0; j < 4; ++j) {
                this.level().addParticle(ParticleTypes.BUBBLE, getX(), getY(), getZ(),
                        movement.x * -0.2, movement.y * -0.2, movement.z * -0.2);
            }
        }

        if (velocityPerc < 0.2F) {
            this.discard();
        } else if (velocityPerc < 0.5F) {
            this.setNoGravity(false);
        }

        this.setDeltaMovement(movement.scale(inertia));
        this.setPos(position().add(movement));
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity target = Services.PLATFORM.getHitEntity(entity);
        Entity owner = this.getOwner();
        boolean raiderFF = owner instanceof Raider && target instanceof Raider;
        boolean playerFF = owner instanceof Player player && target instanceof Player player1 && !player.canHarmPlayer(player1);
        if (raiderFF || playerFF) return;
        DamageSource source = damageSource(this, owner);
        int longshot = weapon != null ? EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.LONGSHOT, weapon) : 0;
        double distance = owner != null ? Mth.clamp(owner.distanceTo(entity), 0, 48) : 0;
        damage = getDamageScaling() * velocityPerc * (target instanceof IronGolem ? 0.25F : 1F);

        if (this.level() instanceof ServerLevel && weapon != null && longshot > 0 && distance >= 16) {
            double coef = Math.max(((0.1953125D * 0.15F * distance * distance) - (3.125D * 0.15F * distance) + (0.15F * 100)) * (1 + 0.66F * (longshot - 1)) / 100, 0);
            damage = damage * (float) (bullet.equals(ModRegistry.ENCHANTED_CARTRIDGE) ? 1 + coef * 1.2 : 1 + coef);
        }

        if (entity.hurt(source, damage) && entity instanceof LivingEntity living) {
            if (level() instanceof ServerLevel && owner instanceof LivingEntity) {
                EnchantmentHelper.doPostHurtEffects(living, owner);
                EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, living);
            }
            this.doPostHurtEffects(living);
        }
        this.discard();
    }

    protected void doPostHurtEffects(LivingEntity entity) {
        if (bullet.equals(ModRegistry.HELLFIRE_CARTRIDGE)) {
            entity.addEffect(new MobEffectInstance(ModRegistry.ARMOR_DECREASE, 600));
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    public DamageSource damageSource(BulletEntity bullet, @Nullable Entity attacker) {
        return ((DamageSourcesInvoker)level().damageSources()).invokeSource(Constants.BULLET, bullet, attacker);
    }

    public Item getBullet() {
        return bullet;
    }

    @Nullable
    public ItemStack getWeapon() {
        return weapon;
    }

    protected float getDamageScaling() {
        if (getOwner() instanceof Mob) {
            float multiplier = switch (level().getDifficulty()) {
                case EASY, PEACEFUL -> 0.5F;
                case NORMAL -> 0.75F;
                default -> 1F;
            };
            return damage * (multiplier * Config.MOB_DAMAGE_MULT.get().floatValue());
        }
        return damage;
    }

    public static float applyArmorPiercing(float damage, float armor, float toughness, BulletEntity bullet) {
        float i = 2.0F + toughness / 4.0F;
        float reduction = Mth.clamp(armor - damage / i, armor * 0.2F, 20.0F);
        float armorCoef = reduction / 25.0F;
        float firepower = bullet.weapon != null ? EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.FIREPOWER, bullet.weapon) * 0.1F : 0F;
        float piercingCoef = bullet.bullet.equals(ModRegistry.ENCHANTED_CARTRIDGE) ? 0.2F + firepower : 0.1F + firepower;
        return damage * (1.0F - Mth.clamp(armorCoef - piercingCoef, 0, 1F));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity owner = getOwner();
        return new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(),
                getXRot(), getYRot(), getType(), owner != null ? owner.getId() : 0,
                getDeltaMovement().scale(3.9F / bullet.VELOCITY), 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 velocity = new Vec3(packet.getXa(), packet.getYa(), packet.getZa());
        setDeltaMovement(velocity.scale(1.0 / 3.9));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!getDeltaMovement().equals(Vec3.ZERO) && level().isClientSide && !speedUpdated) {
            setDeltaMovement(getDeltaMovement().scale(bullet.VELOCITY));
            speedUpdated = true;
        }
    }
}