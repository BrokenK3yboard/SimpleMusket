package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.ModEnchantments;
import com.brokenkeyboard.simplemusket.item.BulletItem;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
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
    private float damage;
    private float velocityPerc = 1F;
    @Nullable
    private ItemStack weapon;
    private boolean speedUpdated = false;

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
        if (MusketItem.BULLETS.test(bullet)) this.bullet = (BulletItem) bullet.getItem();
        this.damage = (float) (owner instanceof Mob ? Config.BULLET_DAMAGE.get() * Config.MOB_DAMAGE_MULT.get() : Config.BULLET_DAMAGE.get());
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

        if (hitresult.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf(hitresult);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected ProjectileDeflection hitTargetOrDeflectSelf(HitResult result) {
        if (result instanceof EntityHitResult entity && entity.getEntity() instanceof IronGolem golem) {
            golem.hurt(damageSource(this, getOwner()), damage * 0.25F);
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
        boolean raiderFF = owner instanceof Raider && target instanceof Raider && this.lastDeflectedBy == null;
        boolean playerFF = owner instanceof Player player && target instanceof Player player1 && !player.canHarmPlayer(player1);
        if (raiderFF || playerFF) return;
        DamageSource source = damageSource(this, owner);
        damage *= velocityPerc;

        if (this.level() instanceof ServerLevel && weapon != null) {
            damage = ModEnchantments.modifyDamageDistance(this.weapon, target, source, damage);
        }

        if (entity.hurt(source, damage) && entity instanceof LivingEntity living) {
            if (bullet.equals(ModRegistry.HELLFIRE_CARTRIDGE)) {
                living.addEffect(new MobEffectInstance(ModRegistry.ARMOR_DECREASE_EFFECT, 600));
            }

            if (level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, living, source, this.getWeaponItem());
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

    public Item getBullet() {
        return bullet;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        Vec3 position = entity.getPositionBase();
        Entity owner = getOwner();
        return new ClientboundAddEntityPacket(getId(), getUUID(), position.x(), position.y(), position.z(),
                entity.getLastSentXRot(), entity.getLastSentYRot(), getType(), owner != null ? owner.getId() : 0,
                entity.getLastSentMovement().scale(3.9F / bullet.VELOCITY), 0);
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