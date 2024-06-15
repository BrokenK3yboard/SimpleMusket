package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class MusketItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> BULLETS = (stack) -> stack.getItem() instanceof BulletItem;

    public MusketItem(Properties properties) {
        super(properties.durability(256));
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

        if ((isLoaded(stack) || (getAllSupportedProjectiles().test(player.getProjectile(stack)) || player.getAbilities().instabuild)) && !player.isEyeInFluid(FluidTags.WATER)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (isLoaded(stack)) {
            double aimPerc = Math.min(((double) (getUseDuration() - timeLeft) / getAim(stack, entity)), 1.0);
            float deviation = (float) (12 - aimPerc * 12);
            fire(level, entity, entity.getUsedItemHand(), stack, 4F, deviation, null, SoundSource.PLAYERS);
        } else if (getUseDuration() - timeLeft >= Config.RELOAD_TIME.get()) {
            if ((entity instanceof Player player && player.getAbilities().instabuild) || !(entity instanceof Player)) {
                setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE));
            } else {
                ItemStack ammo = entity.getProjectile(stack);
                setAmmo(stack, new ItemStack(ammo.getItem()));
                ammo.shrink(1);
                if (ammo.isEmpty()) {
                    ((Player) entity).getInventory().removeItem(ammo);
                }
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        int useTime = getUseDuration() - timeLeft;
        int reloadTime = Config.RELOAD_TIME.get();
        SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;

        if (!isLoaded(stack)) {
            if (Math.floor(reloadTime * 0.12) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_0, source, 1F, 0.8F);
            } else if (Math.floor(reloadTime * 0.6) == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_LOAD_1, source, 1F, 0.8F);
            } else if (reloadTime == useTime) {
                level.playSound(null, entity, ModRegistry.MUSKET_READY, source, 1F, 0.8F);
            }
        }
    }

    public void fire(Level level, LivingEntity entity, InteractionHand hand, ItemStack stack, float power, float deviation, LivingEntity target, SoundSource source) {
        if (!(level instanceof ServerLevel serverLevel) || (entity instanceof Mob mob && mob.getTarget() == null)) return;

        ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (projectiles != null && !projectiles.isEmpty()) {
            this.shoot(serverLevel, entity, hand, stack, projectiles.getItems(), power, deviation, entity instanceof Player, target);
            ItemStack bullet = getLoadedAmmo(stack);
            setAmmo(stack, new ItemStack(stack.getItem(), bullet.getCount() - 1));
        }
        spawnParticles(level, entity, entity instanceof Mob mob ? mobTargetVec(mob, mob.getTarget()) : Vec3.directionFromRotation(entity.getXRot(), entity.getYRot()));
        Services.PLATFORM.playSound(ModRegistry.MUSKET_FIRE, source, (ServerLevel) level, entity.position());
    }

    @Override
    protected void shootProjectile(LivingEntity entity, Projectile projectile, int index, float power, float deviation, float v2, LivingEntity target) {
        projectile.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
        if (entity instanceof Mob mob) {
            // bullet.setDamageScaling(Config.REDUCE_MOB_DAMAGE.get() ? 0.75 : 1); FIX LATER
            Vec3 direction = mobTargetVec(mob, mob.getTarget());
            projectile.shoot(direction.x(), direction.y(), direction.z(), 4F, deviation);
        } else {
            projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0F, 4F, deviation);
        }
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity entity, ItemStack weapon, ItemStack ammo, boolean $$4) {
        Vec3 pos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        if (ammo.getItem() instanceof BulletItem bullet) {
            return new BulletEntity(level, entity, pos, bullet.DAMAGE, bullet.PIERCING, 0, 0, 60, 12F, false, false);
        }
        return new BulletEntity(level, entity, pos, 16, 0.5, 0, 0, 60, 12F, false, false);
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

    public int getAim(ItemStack stack, LivingEntity entity) {
        int deadeye = EnchantmentHelper.getItemEnchantmentLevel(entity.level().registryAccess().registry(Registries.ENCHANTMENT).get().getHolderOrThrow(ModRegistry.DEADEYE), stack);
        return (int) (deadeye > 0 ? Config.AIM_TIME.get() * (1 - (0.125 + 0.125 * deadeye)) : Config.AIM_TIME.get());
    }

    public static void setAmmo(ItemStack stack, ItemStack bullet) {
        if (bullet.getCount() < 1) {
            stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        } else {
            stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(bullet));
        }
    }

    public static ItemStack getLoadedAmmo(ItemStack stack) {
        ChargedProjectiles projectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        ItemStack stack1 = projectiles.getItems().getFirst();
        return stack.getItem() instanceof MusketItem musket && musket.getAllSupportedProjectiles().test(stack1) ? stack1 : ItemStack.EMPTY;
    }

    public static boolean isLoaded(ItemStack stack) {
        return !getLoadedAmmo(stack).is(ItemStack.EMPTY.getItem());
    }

    public static float getReloadPerc(ItemStack stack, float useTime) {
        return stack.getItem() instanceof MusketItem ? useTime / Config.RELOAD_TIME.get() : 0;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltip, List<Component> components, TooltipFlag flag) {
        ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (projectiles != null && !projectiles.isEmpty()) {
            ItemStack stack1 = projectiles.getItems().getFirst();
            components.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(stack1.getDisplayName()));
        }
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return isLoaded(stack) && isLoaded(stack) ? UseAnim.BOW : UseAnim.NONE;
    }

    public int getUseDuration() {
        return 72000;
    }
}