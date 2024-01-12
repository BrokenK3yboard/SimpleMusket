package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class MusketItem extends FirearmItem {

    public static final Predicate<ItemStack> BULLETS = (stack) -> stack.getItem() instanceof BulletItem;
    private static final int DURABILITY = 256;

    public MusketItem(Properties properties) {
        super(properties.defaultDurability(DURABILITY));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return BULLETS;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 30;
    }

    @Override
    public int getReload(ItemStack stack) {
        double coeff = 1 - (EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack) * 0.1);
        return (int) (Config.MUSKET_RELOAD_TIME.get() * coeff);
    }

    @Override
    public int getAim(ItemStack stack) {
        double coeff = 1 - (EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack) * 0.1);
        return (int) (Config.MUSKET_AIM_TIME.get() * coeff);
    }

    @Override
    public float getDeviation() {
        return 12.0F;
    }

    @Override
    public SoundEvent getFireSound() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    public void createProjectile(LivingEntity entity, Level level, ItemStack stack, float deviation) {
        if (level.isClientSide) return;

        Vec3 initialPos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        ArrayList<BulletEntity> projectiles = new ArrayList<>();
        BulletItem bulletItem = getAmmo(stack).getItem() instanceof BulletItem bulletItem1 ? bulletItem1 : (BulletItem) SimpleMusket.IRON_BULLET.get().asItem();
        int firepower = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.FIREPOWER.get(), stack);
        int longshot = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.LONGSHOT.get(), stack);
        int deadeye = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack);
        double damage = bulletItem == SimpleMusket.GOLD_BULLET.get() ? getGoldBulletDamage(stack.getAllEnchantments(), bulletItem.DAMAGE) : bulletItem.DAMAGE;
        double piercing = Math.min(bulletItem.PIERCING + firepower * 0.1, 1);
        boolean noIframe = bulletItem == SimpleMusket.COPPER_BULLET.get();
        boolean enchanted = ModList.get().isLoaded("consecration") && bulletItem == SimpleMusket.GOLD_BULLET.get() && !stack.getAllEnchantments().isEmpty();
        deviation = bulletItem == SimpleMusket.COPPER_BULLET.get() ? (float) (getDeviation() * deadeye * 0.1) : deviation;

        for (int i = 0; i < (bulletItem == SimpleMusket.COPPER_BULLET.get() ? 5 : 1); i++) {
            projectiles.add(new BulletEntity(level, initialPos, (float) damage, piercing, longshot, noIframe, enchanted));
        }

        for (BulletEntity bullet : projectiles) {
            bullet.setOwner(entity);
            bullet.setPos(initialPos);

            if (entity instanceof MusketPillager musketAttackMob) {
                bullet.setDamageScaling(Config.REDUCE_PILLAGER_DAMAGE.get() ? 0.75 : 1);
                musketAttackMob.shootBullet(entity, Objects.requireNonNull(musketAttackMob.getTarget()), bullet, 4F);
            } else {
                bullet.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0F, 4F, deviation);
            }
            level.addFreshEntity(bullet);
        }
        stack.hurtAndBreak(bulletItem == SimpleMusket.NETHERITE_BULLET.get() ? 3 : 1, entity, (user) -> user.broadcastBreakEvent(user.getUsedItemHand()));
    }

    private static double getGoldBulletDamage(Map<Enchantment, Integer> enchantments, double baseDamage) {
        if (ModList.get().isLoaded("consecration") && Config.CONSECRATION_COMPAT.get()) return baseDamage;
        int value = 0;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            switch(entry.getKey().getRarity()) {
                case UNCOMMON -> value += (entry.getValue() * 2);
                case RARE -> value += (entry.getValue() * 3);
                case VERY_RARE -> value += (entry.getValue() * 4);
                default -> value += (entry.getValue());
            }
        }
        return Math.min((value / 2) + 1, 15) + baseDamage;
    }
}