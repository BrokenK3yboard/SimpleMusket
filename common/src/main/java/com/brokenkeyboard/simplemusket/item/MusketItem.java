package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.enchantment.ModEnchantments;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MusketItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> BULLETS = (stack) -> stack.getItem() instanceof BulletItem;

    public MusketItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return BULLETS;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 40;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if ((isLoaded(stack) || !player.getProjectile(stack).isEmpty()) && !player.isEyeInFluid(FluidTags.WATER) && !player.isEyeInFluid(FluidTags.LAVA)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity.isEyeInFluid(FluidTags.WATER) || entity.isEyeInFluid(FluidTags.LAVA)) return;
        if (isLoaded(stack)) {
            int aimTicks = getUseDuration(stack, entity) - timeLeft;
            float velocity = ((BulletItem) getLoadedAmmo(stack).getItem()).VELOCITY;
            float inaccuracy = 12 - Math.clamp(aimTicks / Config.AIM_TIME.get(), 0, 1) * 12;
            fire(level, entity, entity.getUsedItemHand(), stack, velocity, inaccuracy, null, SoundSource.PLAYERS);
        } else if (getUseDuration(stack, entity) - timeLeft >= Config.RELOAD_TIME.get()) {
            List<ItemStack> projectiles = draw(stack, entity.getProjectile(stack), entity);
            if (!projectiles.isEmpty()) {
                ItemStack stack1 = projectiles.getFirst();
                Item bullet = getAllSupportedProjectiles().test(stack1) ? stack1.getItem() : ModRegistry.CARTRIDGE;
                int amount = level instanceof ServerLevel ? ModEnchantments.modifyAmmoCount(stack, 1) : 1;
                setAmmo(stack, new ItemStack(bullet, amount));
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity.isEyeInFluid(FluidTags.WATER) || entity.isEyeInFluid(FluidTags.LAVA)) entity.stopUsingItem();
        int useTime = getUseDuration(stack, entity) - timeLeft;

        SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
        if (!isLoaded(stack)) {
            if (Math.floor(Config.RELOAD_TIME.get() * 0.12) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_0, source, 1F, 0.8F);
            } else if (Math.floor(Config.RELOAD_TIME.get() * 0.6) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_1, source, 1F, 0.8F);
            } else if (Config.RELOAD_TIME.get() == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_READY, source, 1F, 0.8F);
            }
        }
    }

    public void fire(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float velocity, float inaccuracy, @Nullable LivingEntity target, SoundSource source) {
        if (!(level instanceof ServerLevel serverLevel) || (shooter instanceof Mob && target == null)) return;
        ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (projectiles != null && !projectiles.isEmpty()) {
            this.shoot(serverLevel, shooter, hand, stack, projectiles.getItems(), velocity, inaccuracy, shooter instanceof Player, target);
            ItemStack bullet = getLoadedAmmo(stack);
            setAmmo(stack, new ItemStack(bullet.getItem(), bullet.getCount() - 1));
        }

        Vec3 direction = shooter instanceof Mob ? mobTargetVec(shooter, target) : Vec3.directionFromRotation(shooter.getXRot(), shooter.getYRot());
        Vec3 side = Vec3.directionFromRotation(0, shooter.getYRot() + (hand == InteractionHand.MAIN_HAND ? 90 : -90));
        Vec3 down = Vec3.directionFromRotation(shooter.getXRot() + 90, shooter.getYRot());
        Vec3 smokePos = shooter.getEyePosition().add(side.add(down).scale(0.15));
        spawnParticles(level, smokePos, direction);
        Services.PLATFORM.playSound(source, serverLevel, shooter.position());
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        if (shooter instanceof Mob && target != null) {
            Vec3 direction = mobTargetVec(shooter, target);
            projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);
        } else if (shooter instanceof Player) {
            projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0F, velocity, inaccuracy);
        }
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isPlayer) {
        Vec3 origin = new Vec3(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        return new BulletEntity(level, shooter, origin, ammo, weapon);
    }

    public static Vec3 mobTargetVec(LivingEntity mob, LivingEntity target) {
        double vecY = target.getBoundingBox().minY + target.getBbHeight() * 0.7f - mob.getY() - mob.getEyeHeight();
        return new Vec3(target.getX() - mob.getX(), vecY, target.getZ() - mob.getZ()).normalize();
    }

    public static void spawnParticles(Level level, Vec3 position, Vec3 direction) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        RandomSource random = level.getRandom();

        for (int i = 0; i < 10; i++) {
            double t = Math.pow(random.nextFloat(), 1.5);
            Vec3 smokePos = position.add(direction.scale(1.25 + t));
            smokePos = smokePos.add(new Vec3(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5).scale(0.1));
            Vec3 smokeVec = direction.scale(0.1 * (1 - t));
            serverLevel.sendParticles(ParticleTypes.POOF, smokePos.x, smokePos.y, smokePos.z, 0, smokeVec.x, smokeVec.y, smokeVec.z, 1);
        }
    }

    public static void setAmmo(ItemStack stack, ItemStack bullet) {
        stack.set(DataComponents.CHARGED_PROJECTILES, bullet.getCount() < 1 ? ChargedProjectiles.EMPTY : ChargedProjectiles.of(bullet));
    }

    public static ItemStack getLoadedAmmo(ItemStack stack) {
        ChargedProjectiles projectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return (!(stack.getItem() instanceof MusketItem) || projectiles.isEmpty()) ? ItemStack.EMPTY : projectiles.getItems().getFirst();
    }

    public static boolean isLoaded(ItemStack stack) {
        return BULLETS.test(getLoadedAmmo(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltip, List<Component> components, TooltipFlag flag) {
        ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (projectiles != null && !projectiles.isEmpty()) {
            ItemStack stack1 = projectiles.getItems().getFirst();
            components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(stack1.getDisplayName()));
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return isLoaded(stack) ? UseAnim.BOW : UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
}