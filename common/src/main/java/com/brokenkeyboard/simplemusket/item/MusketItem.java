package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MusketItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> BULLETS = (stack) -> stack.getItem() instanceof BulletItem;

    public MusketItem(Properties properties) {
        super(properties.defaultDurability(256));
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
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
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
            int aimTicks = getUseDuration(stack) - timeLeft;
            float velocity = ((BulletItem) getLoadedAmmo(stack).getItem()).VELOCITY;
            float inaccuracy = 12 - Mth.clamp(aimTicks / Config.AIM_TIME.get(), 0, 1) * 12;
            fire(level, entity, entity.getUsedItemHand(), stack, velocity, inaccuracy, null, SoundSource.PLAYERS);
        } else if (getUseDuration(stack) - timeLeft >= Config.RELOAD_TIME.get()) {
            int amount = getAmmoCount(stack);
            if (entity instanceof Mob) {
                setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, amount));
            } else if (entity instanceof Player player) {
                ItemStack ammo = player.getProjectile(stack);
                if (player.getAbilities().instabuild) {
                    setAmmo(stack, new ItemStack(getAllSupportedProjectiles().test(ammo) ? ammo.getItem() : ModRegistry.CARTRIDGE, amount));
                } else if (!ammo.isEmpty() && getAllSupportedProjectiles().test(ammo)) {
                    setAmmo(stack, new ItemStack(ammo.getItem(), amount));
                    ammo.shrink(1);
                    if (ammo.isEmpty()) (player).getInventory().removeItem(ammo);
                }
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity.isEyeInFluid(FluidTags.WATER) || entity.isEyeInFluid(FluidTags.LAVA)) entity.stopUsingItem();
        int useTime = getUseDuration(stack) - timeLeft;

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

        ItemStack bulletStack = getLoadedAmmo(stack);
        Vec3 pos = new Vec3(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        int ammoCount = bulletStack.getCount();
        BulletEntity bullet = new BulletEntity(serverLevel, shooter, pos, bulletStack, stack);
        bullet.setPos(pos);

        if (shooter instanceof Mob mob) {
            Vec3 direction = mobTargetVec(mob, target);
            bullet.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);
        } else {
            bullet.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0F, velocity, inaccuracy);
        }

        level.addFreshEntity(bullet);
        setAmmo(stack, new ItemStack(bulletStack.getItem(), ammoCount - 1));

        Vec3 direction = shooter instanceof Mob ? mobTargetVec(shooter, target) : Vec3.directionFromRotation(shooter.getXRot(), shooter.getYRot());
        Vec3 side = Vec3.directionFromRotation(0, shooter.getYRot() + (hand == InteractionHand.MAIN_HAND ? 90 : -90));
        Vec3 down = Vec3.directionFromRotation(shooter.getXRot() + 90, shooter.getYRot());
        Vec3 smokePos = shooter.getEyePosition().add(side.add(down).scale(0.15));
        spawnParticles(level, smokePos, direction);
        Services.PLATFORM.playSound(ModRegistry.MUSKET_FIRE, source, serverLevel, shooter.position());
        stack.hurtAndBreak(1, shooter, (user) -> user.broadcastBreakEvent(user.getUsedItemHand()));
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

    public static Vec3 mobTargetVec(LivingEntity mob, LivingEntity target) {
        double vecY = target.getBoundingBox().minY + target.getBbHeight() * 0.7f - mob.getY() - mob.getEyeHeight();
        return new Vec3(target.getX() - mob.getX(), vecY, target.getZ() - mob.getZ()).normalize();
    }

    public static void setAmmo(ItemStack stack, ItemStack bullet) {
        if (bullet.getCount() < 1) {
            stack.removeTagKey("LoadedProjectiles");
        } else {
            CompoundTag tag = new CompoundTag();
            stack.addTagElement("LoadedProjectiles", bullet.save(tag));
        }
    }

    public static ItemStack getLoadedAmmo(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("LoadedProjectiles");
        boolean valid = tag != null && stack.getItem() instanceof MusketItem;
        return valid && ((MusketItem)stack.getItem()).getAllSupportedProjectiles().test(ItemStack.of(tag)) ? ItemStack.of(tag) : ItemStack.EMPTY;
    }

    public static int getAmmoCount(ItemStack stack) {
        int repeating = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.REPEATING, stack);
        if (repeating > 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                int amount = tag.getInt("AmmoBonus");
                stack.removeTagKey("AmmoBonus");
                return repeating + amount + 1;
            } else {
                return repeating + 1;
            }
        }
        return 1;
    }

    public static boolean hasAmmo(ItemStack stack) {
        return !getLoadedAmmo(stack).is(ItemStack.EMPTY.getItem());
    }

    public static boolean isLoaded(ItemStack stack) {
        return BULLETS.test(getLoadedAmmo(stack));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (hasAmmo(stack)) components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(getLoadedAmmo(stack).getDisplayName()));
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return hasAmmo(stack) ? UseAnim.BOW : UseAnim.NONE;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}