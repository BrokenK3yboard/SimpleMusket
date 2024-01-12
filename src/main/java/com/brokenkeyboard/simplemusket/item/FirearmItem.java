package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FirearmItem extends ProjectileWeaponItem {

    public abstract int getReload(ItemStack stack);
    public abstract int getAim(ItemStack stack);
    public abstract float getDeviation();
    public abstract SoundEvent getFireSound();
    public abstract void createProjectile(LivingEntity entity, Level Level, ItemStack stack, float deviation);

    public FirearmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = player.getProjectile(stack).getItem() instanceof BulletItem || player.getAbilities().instabuild;

        if ((hasAmmo || hasAmmo(stack)) && !player.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            if (hasAmmo(stack) && !isLoaded(stack)) {
                setLoaded(stack, true);
            }
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

            createProjectile(player, level, stack, deviation);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), getFireSound(), SoundSource.PLAYERS, 0.8F, 1F);

            int ammoCount = getAmmo(stack).getCount();
            setAmmo(stack, new ItemStack(FirearmItem.getAmmo(stack).getItem(), ammoCount - 1));
            setLoaded(stack, ammoCount > 1);
            player.stopUsingItem();

        } else if (hasAmmo(stack)) {
            setLoaded(stack, true);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        if (getUseDuration(stack) - timeLeft >= getReload(stack) && !hasAmmo(stack)) {
            ItemStack ammoStack = player.getProjectile(stack);
            int ammo = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.REPEATING.get(), stack) + 1;

            if (player.getAbilities().instabuild) {
                setAmmo(stack, ammoStack.getItem() instanceof BulletItem ? new ItemStack(ammoStack.getItem(), ammo) : new ItemStack(SimpleMusket.IRON_BULLET.get(), ammo));
            } else if (!ammoStack.isEmpty()) {
                setAmmo(stack, new ItemStack(ammoStack.getItem(), ammo));
                ammoStack.shrink(1);
                if (ammoStack.isEmpty()) player.getInventory().removeItem(ammoStack);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1F, 1.1F);
        }
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return (hasAmmo(stack) && isLoaded(stack)) ? UseAnim.BOW : UseAnim.NONE;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (hasAmmo(stack)) components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(getAmmo(stack).getDisplayName()));
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
        return (tag == null ? ItemStack.EMPTY : (ItemStack.of(tag).getItem() instanceof BulletItem) ? ItemStack.of(tag) : ItemStack.EMPTY);
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