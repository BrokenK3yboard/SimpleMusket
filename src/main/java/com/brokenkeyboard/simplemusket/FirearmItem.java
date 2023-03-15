package com.brokenkeyboard.simplemusket;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FirearmItem extends Item {

    protected abstract boolean isAmmo(ItemStack itemstack);
    protected abstract int getReload(ItemStack stack);
    protected abstract int getAim(ItemStack stack);
    protected abstract float getDeviation(ItemStack stack);
    protected abstract SoundEvent getFireSound();
    protected abstract void createProjectile(Player player, Level Level, ItemStack stack, float deviation);

    public FirearmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasAmmo = !findAmmo(player).isEmpty() || player.isCreative();

        if (player.isEyeInFluid(FluidTags.WATER) && !player.isCreative()) {
            return InteractionResultHolder.fail(stack);
        } else if (hasAmmo || isLoaded(stack)) {
            if(isLoaded(stack) && !isReady(stack)) {
                setReady(stack, true);
                int extraAmmo = EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.REPEATING.get(), stack);
                setExtraAmmo(stack, extraAmmo);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return;

        int repeatingLevel = EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.REPEATING.get(), stack);

        if (isLoaded(stack) && isReady(stack)) {
            double coefficient = Math.min(((double) (getUseDuration(stack) - timeLeft) / getAim(stack)), 1.0);
            float accuracy = (float) (coefficient * getDeviation(stack));
            float deviation = getDeviation(stack) - accuracy;

            createProjectile(player, level, stack, deviation);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), getFireSound(), SoundSource.PLAYERS, 0.8F, 1F);

            if(repeatingLevel > 0 && getExtraAmmo(stack) > 0) {
                setExtraAmmo(stack, getExtraAmmo(stack) - 1);
            } else {
                player.stopUsingItem();
                setReady(stack, false);
                setAmmoType(stack, 0);
            }
        } else if (isLoaded(stack)) {
            setReady(stack, true);
            setExtraAmmo(stack, repeatingLevel);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return;

        if (getUseDuration(stack) - timeLeft >= getReload(stack) && !isLoaded(stack)) {
            ItemStack ammoStack = findAmmo(player);

            if (player.isCreative()) {
                if (!ammoStack.isEmpty())
                    setAmmoType(stack, ((BulletItem) ammoStack.getItem()).getType());
                else
                    setAmmoType(stack, 1);
            } else {
                if (ammoStack.isEmpty()) return;
                setAmmoType(stack, ((BulletItem) ammoStack.getItem()).getType());
                ammoStack.shrink(1);
                if (ammoStack.isEmpty()) player.getInventory().removeItem(ammoStack);
            }
            entityLiving.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1F, 1.1F);
        }
    }

    private ItemStack findAmmo(Player player) {
        for (int i = 0; i != player.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = player.getInventory().getItem(i);
            if (isAmmo(itemstack))
                return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        if(isLoaded(stack) && isReady(stack)) return UseAnim.BOW;
        return UseAnim.NONE;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (!isLoaded(stack)) return;
        String name = BulletType.values()[getAmmoType(stack)].toString().toLowerCase() + "_bullet";
        String displayName = "item." + SimpleMusket.MOD_ID + "." + name;
        components.add(Component.translatable("item.minecraft.crossbow.projectile").append(" [").append(Component.translatable(displayName)).append("]"));
    }

    private static void setAmmoType(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt("ammotype", value);
    }

    public static int getAmmoType(ItemStack stack) {
        return stack.getOrCreateTag().getInt("ammotype");
    }

    private static void setExtraAmmo(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt("extraammo", value);
    }

    public static int getExtraAmmo(ItemStack stack) {
        return stack.getOrCreateTag().getInt("extraammo");
    }

    private static void setReady(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean("ready", value);
    }

    public static boolean isReady(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("ready");
    }

    public static boolean isLoaded(ItemStack stack) {
        return getAmmoType(stack) > 0;
    }

    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }
}