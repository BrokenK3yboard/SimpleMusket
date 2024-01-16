package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Config;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.network.Network;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import static com.brokenkeyboard.simplemusket.SimpleMusket.CONSECRATION;

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
    public Item getDefaultAmmo() {
        return SimpleMusket.IRON_BULLET.get();
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
    public void fireWeapon(LivingEntity entity, Level level, SoundSource source, ItemStack stack, float deviation) {
        if (level.isClientSide || (entity instanceof Mob mob && mob.getTarget() == null)) return;

        Vec3 initialPos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        ArrayList<BulletEntity> projectiles = new ArrayList<>();
        ItemStack bulletStack = getAmmo(stack);
        BulletItem bulletItem = (BulletItem) bulletStack.getItem();

        int ammoCount = bulletStack.getCount();
        int firepower = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.FIREPOWER.get(), stack);
        int longshot = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.LONGSHOT.get(), stack);
        int deadeye = EnchantmentHelper.getTagEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack);

        double damage = bulletItem == SimpleMusket.GOLD_BULLET.get() ? getGoldBulletDamage(stack.getAllEnchantments(), bulletItem.DAMAGE) : bulletItem.DAMAGE;
        double piercing = Math.min(bulletItem.PIERCING + firepower * 0.1, 1);
        boolean noIFrames = bulletItem == SimpleMusket.COPPER_BULLET.get();
        boolean isHoly = CONSECRATION && bulletItem == SimpleMusket.GOLD_BULLET.get() && !stack.getAllEnchantments().isEmpty();
        deviation = bulletItem == SimpleMusket.COPPER_BULLET.get() ? (float) (getDeviation() * deadeye * 0.1) : deviation;

        for (int i = 0; i < (bulletItem == SimpleMusket.COPPER_BULLET.get() ? 5 : 1); i++) {
            projectiles.add(new BulletEntity(level, entity, initialPos, (float) damage, piercing, longshot, noIFrames, isHoly));
        }

        for (BulletEntity bullet : projectiles) {
            bullet.setPos(initialPos);
            if (entity instanceof Mob mob) {
                bullet.setDamageScaling(Config.REDUCE_PILLAGER_DAMAGE.get() ? 0.75 : 1);
                Vec3 direction = mobTargetVec(mob, mob.getTarget());
                bullet.shoot(direction.x(), direction.y(), direction.z(), 4F, deviation);
            } else {
                bullet.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0F, 4F, deviation);
            }
            level.addFreshEntity(bullet);
        }

        setAmmo(stack, new ItemStack(bulletItem, ammoCount - 1));
        setLoaded(stack, ammoCount > 1);
        spawnParticles(level, entity, entity instanceof Mob mob ? mobTargetVec(mob, mob.getTarget()) : Vec3.directionFromRotation(entity.getXRot(), entity.getYRot()));
        Network.S2CSound(SimpleMusket.MUSKET_FIRE.get(), source, level.dimension(), entity.blockPosition());
        stack.hurtAndBreak(bulletItem == SimpleMusket.NETHERITE_BULLET.get() ? 3 : 1, entity, (user) -> user.broadcastBreakEvent(user.getUsedItemHand()));
    }

    private static double getGoldBulletDamage(Map<Enchantment, Integer> enchantments, double baseDamage) {
        if (CONSECRATION && Config.CONSECRATION_COMPAT.get()) return baseDamage;
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