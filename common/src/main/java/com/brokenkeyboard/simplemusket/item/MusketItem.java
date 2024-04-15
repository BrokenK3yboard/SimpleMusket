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
import java.util.ArrayList;
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
        return 24;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = getAllSupportedProjectiles().test(player.getProjectile(stack)) || player.getAbilities().instabuild;

        if ((hasAmmo || hasAmmo(stack)) && !player.isEyeInFluid(FluidTags.WATER)) {
            if (hasAmmo(stack) && !isLoaded(stack)) setLoaded(stack, true);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!hasAmmo(stack)) return;
        if (isLoaded(stack)) {
            double aimPerc = Math.min(((double) (getUseDuration(stack) - timeLeft) / getAim(stack)), 1.0);
            float deviation = (float) (12 - aimPerc * 12);
            fire(entity, level, SoundSource.PLAYERS, stack, deviation);
        } else {
            setLoaded(stack, true);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        int useTime = getUseDuration(stack) - timeLeft;
        int reloadTime = Config.RELOAD_TIME.get();
        SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;

        if (useTime >= reloadTime && !hasAmmo(stack)) {
            ItemStack ammo = entity.getProjectile(stack);
            int amount = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.REPEATING, stack) + 1;

            if (entity instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    setAmmo(stack, new ItemStack(getAllSupportedProjectiles().test(ammo) ? ammo.getItem() : ModRegistry.CARTRIDGE, amount));
                } else if (!ammo.isEmpty()) {
                    setAmmo(stack, new ItemStack(ammo.getItem(), amount));
                    ammo.shrink(1);
                    if (ammo.isEmpty()) (player).getInventory().removeItem(ammo);
                }
            } else {
                setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE, amount));
            }

            level.playSound(null, entity, ModRegistry.MUSKET_READY, source, 1F, 0.8F);
        } else if (!isLoaded(stack)) {
            if (Math.floor(reloadTime * 0.12) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_0, source, 1F, 0.8F);
            } else if (Math.floor(reloadTime * 0.6) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_1, source, 1F, 0.8F);
            }
        }
    }

    public void fire(LivingEntity entity, Level level, SoundSource source, ItemStack stack, double deviation) {
        if (level.isClientSide || (entity instanceof Mob mob && mob.getTarget() == null)) return;

        Vec3 pos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        ArrayList<BulletEntity> projectiles = new ArrayList<>();
        ItemStack bulletStack = getAmmo(stack);
        BulletItem bulletItem = (BulletItem) bulletStack.getItem();
        int ammoCount = bulletStack.getCount();
        int longshot = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.LONGSHOT, stack);
        int blast = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.BLAST, stack);
        double damage = bulletItem.DAMAGE;
        double piercing = Math.min(bulletItem.PIERCING + EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.FIREPOWER, stack) * 0.1, 1);
        boolean isHoly = bulletItem == ModRegistry.ENCHANTED_CARTRIDGE;
        boolean isHellfire = bulletItem == ModRegistry.HELLFIRE_CARTRIDGE;

        for (int i = 0; i < (blast > 0 ? 4 * blast : 1); i++) {
            projectiles.add(new BulletEntity(level, entity, pos, (i > 0 ? damage * 0.25 : damage), piercing, longshot, (i > 0 ? 0 : blast), (i > 0 ? 12 : 60), (i > 0 ? 18F : deviation), isHoly, isHellfire));
        }

        for (BulletEntity bullet : projectiles) {
            bullet.setPos(pos);
            if (entity instanceof Mob mob) {
                bullet.setDamageScaling(Config.REDUCE_MOB_DAMAGE.get() ? 0.75 : 1);
                Vec3 direction = mobTargetVec(mob, mob.getTarget());
                bullet.shoot(direction.x(), direction.y(), direction.z(), 4F, bullet.getDeviation());
            } else {
                bullet.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0F, 4F, bullet.getDeviation());
            }
            level.addFreshEntity(bullet);
        }

        setAmmo(stack, new ItemStack(bulletItem, ammoCount - 1));
        setLoaded(stack, ammoCount > 1);
        spawnParticles(level, entity, entity instanceof Mob mob ? mobTargetVec(mob, mob.getTarget()) : Vec3.directionFromRotation(entity.getXRot(), entity.getYRot()));
        Services.PLATFORM.playSound(ModRegistry.MUSKET_FIRE, source, (ServerLevel) level, entity.position());
        stack.hurtAndBreak(bulletItem == ModRegistry.HELLFIRE_CARTRIDGE ? 3 : 1, entity, (user) -> user.broadcastBreakEvent(user.getUsedItemHand()));
    }

    public static void spawnParticles(Level level, LivingEntity entity, Vec3 direction) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 side = Vec3.directionFromRotation(0, entity.getYRot() + (entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? 90 : -90));
        Vec3 down = Vec3.directionFromRotation(entity.getXRot() + 90, entity.getYRot());
        Vec3 pos = entity.getEyePosition().add(side.add(down).scale(0.15));

        for (int i = 0; i < 10; i++) {
            RandomSource random = entity.getRandom();
            double t = Math.pow(random.nextFloat(), 1.5);
            Vec3 p = pos.add(direction.scale(1.25 + t));
            p = p.add(new Vec3(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5).scale(0.1));
            Vec3 v = direction.scale(0.1 * (1 - t));
            serverLevel.sendParticles(ParticleTypes.POOF, p.x, p.y, p.z, (int)v.x, v.y, v.z, 0, 1);
        }
    }

    public static Vec3 mobTargetVec(LivingEntity mob, LivingEntity target) {
        double vecY = target.getBoundingBox().minY + target.getBbHeight() * 0.7f - mob.getY() - mob.getEyeHeight();
        return new Vec3(target.getX() - mob.getX(), vecY, target.getZ() - mob.getZ()).normalize();
    }

    public int getAim(ItemStack stack) {
        int deadeye = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.DEADEYE, stack);
        return (int) (deadeye > 0 ? Config.AIM_TIME.get() * (1 - (0.125 + 0.125 * deadeye)) : Config.AIM_TIME.get());
    }

    public static void setAmmo(ItemStack stack, ItemStack bullet) {
        if (bullet.getCount() < 1) {
            stack.removeTagKey("LoadedProjectiles");
        } else {
            CompoundTag tag = new CompoundTag();
            stack.addTagElement("LoadedProjectiles", bullet.save(tag));
        }
    }

    public static ItemStack getAmmo(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("LoadedProjectiles");
        boolean valid = tag != null && stack.getItem() instanceof MusketItem;
        return valid && ((MusketItem)stack.getItem()).getAllSupportedProjectiles().test(ItemStack.of(tag)) ? ItemStack.of(tag) : ItemStack.EMPTY;
    }

    public static boolean hasAmmo(ItemStack stack) {
        return !getAmmo(stack).is(ItemStack.EMPTY.getItem());
    }

    public static void setLoaded(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean("Loaded", value);
    }

    public static boolean isLoaded(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Loaded");
    }

    public static float getReloadPerc(ItemStack stack, float useTime) {
        return stack.getItem() instanceof MusketItem ? useTime / Config.RELOAD_TIME.get() : 0;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (hasAmmo(stack)) components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(getAmmo(stack).getDisplayName()));
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return hasAmmo(stack) && isLoaded(stack) ? UseAnim.BOW : UseAnim.NONE;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}