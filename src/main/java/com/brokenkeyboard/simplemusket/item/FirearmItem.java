package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FirearmItem extends ProjectileWeaponItem {

    public abstract int getReload(ItemStack stack);
    public abstract int getAim(ItemStack stack);
    public abstract float getDeviation();
    public abstract Item getDefaultAmmo();
    public abstract void fireWeapon(LivingEntity entity, Level Level, SoundSource source, ItemStack stack, float deviation);

    public FirearmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = getAllSupportedProjectiles().test(player.getProjectile(stack)) || player.getAbilities().instabuild;

        if ((hasAmmo || hasAmmo(stack)) && !player.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            if (hasAmmo(stack) && !isLoaded(stack)) setLoaded(stack, true);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        if (hasAmmo(stack) && isLoaded(stack)) {
            double coefficient = Math.min(((double) (getUseDuration(stack) - timeLeft) / getAim(stack)), 1.0);
            float accuracy = (float) (coefficient * getDeviation());
            float deviation = getDeviation() - accuracy;

            fireWeapon(player, level, SoundSource.PLAYERS, stack, deviation);

        } else if (hasAmmo(stack)) {
            setLoaded(stack, true);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (getUseDuration(stack) - timeLeft >= getReload(stack) && !hasAmmo(stack)) {
            SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            ItemStack ammo = entity.getProjectile(stack);
            int amount = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.REPEATING.get(), stack) + 1;

            if (entity instanceof Player player && player.getAbilities().instabuild || entity instanceof Mob) {
                setAmmo(stack, getAllSupportedProjectiles().test(ammo) ? new ItemStack(ammo.getItem(), amount) : new ItemStack(getDefaultAmmo(), amount));
            } else if (entity instanceof Player player && !ammo.isEmpty()) {
                setAmmo(stack, new ItemStack(ammo.getItem(), amount));
                ammo.shrink(1);
                if (ammo.isEmpty()) player.getInventory().removeItem(ammo);
            }
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LEVER_CLICK, source, 1F, 1.1F);
        }
    }

    public static void spawnParticles(Level level, LivingEntity entity, Vec3 direction) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 side = Vec3.directionFromRotation(0, entity.getYRot() + (entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? 90 : -90));
        Vec3 down = Vec3.directionFromRotation(entity.getXRot() + 90, entity.getYRot());
        Vec3 particlePos = entity.getEyePosition().add(side.add(down).scale(0.15));

        for (int i = 0; i < 10; i++) {
            RandomSource random = entity.getRandom();
            double t = Math.pow(random.nextFloat(), 1.5);
            Vec3 p = particlePos.add(direction.scale(1.25 + t));
            p = p.add(new Vec3(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5).scale(0.1));
            Vec3 v = direction.scale(0.1 * (1 - t));
            serverLevel.sendParticles(ParticleTypes.POOF, p.x, p.y, p.z, (int)v.x, v.y, v.z, 0, 1);
        }
    }

    public static Vec3 mobTargetVec(LivingEntity mob, LivingEntity target) {
        double vecY = target.getBoundingBox().minY + target.getBbHeight() * 0.7f - mob.getY() - mob.getEyeHeight();
        return new Vec3(target.getX() - mob.getX(), vecY, target.getZ() - mob.getZ()).normalize();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return (hasAmmo(stack) && isLoaded(stack)) ? UseAnim.BOW : UseAnim.NONE;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (hasAmmo(stack)) {
            components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(getAmmo(stack).getDisplayName()));
        }
    }

    public static void setAmmo(ItemStack stack, ItemStack bullet) {
        if (bullet.getCount() < 1) {
            stack.removeTagKey("LoadedProjectiles");
        } else {
            stack.addTagElement("LoadedProjectiles", bullet.serializeNBT());
        }
    }

    public static ItemStack getAmmo(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("LoadedProjectiles");
        boolean valid = tag != null && stack.getItem() instanceof FirearmItem;
        return valid && ((FirearmItem)stack.getItem()).getAllSupportedProjectiles().test(ItemStack.of(tag)) ? ItemStack.of(tag) : ItemStack.EMPTY;
    }

    public static void setLoaded(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean("Loaded", value);
    }

    public static boolean isLoaded(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Loaded");
    }

    public static float getReloadPerc(ItemStack stack, float useTime) {
        return stack.getItem() instanceof FirearmItem firearm ? useTime / firearm.getReload(stack) : 0;
    }

    public static boolean hasAmmo(ItemStack stack) {
        return !getAmmo(stack).is(ItemStack.EMPTY.getItem());
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}