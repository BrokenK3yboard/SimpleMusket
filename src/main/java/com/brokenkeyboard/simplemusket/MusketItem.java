package com.brokenkeyboard.simplemusket;

import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusketItem extends FirearmItem {

    private static final int DURABILITY = 256;

    public MusketItem(Properties properties) {
        super(properties.defaultDurability(DURABILITY));
    }

    @Override
    protected boolean isAmmo(ItemStack itemstack) {
        return itemstack.getItem() instanceof BulletItem;
    }

    @Override
    protected int getReload(ItemStack stack) {
        return (int) (Config.MUSKET_RELOAD_TIME.get() - (Config.MUSKET_RELOAD_TIME.get() * 0.1 * EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack)));
    }

    @Override
    protected int getAim(ItemStack stack) {
        return (int) (Config.MUSKET_AIM_TIME.get() - (Config.MUSKET_AIM_TIME.get() * 0.1 * EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.DEADEYE.get(), stack)));
    }

    @Override
    protected float getDeviation(ItemStack stack) {
        return 12.0F;
    }

    @Override
    protected SoundEvent getFireSound() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    protected void createProjectile(Player player, Level level, ItemStack stack, float deviation) {
        if (level.isClientSide) return;

        int pierceLevel = EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.FIREPOWER.get(), stack);
        int longshotLevel = EnchantmentHelper.getItemEnchantmentLevel(SimpleMusket.LONGSHOT.get(), stack);
        Vec3 initialPos = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        ArrayList<BulletEntity> projectiles = new ArrayList<>();
        BulletType type = BulletType.values()[getAmmoType(stack)];

        switch (type) {
            case COPPER -> {
                for (int i = 0; i < 5; i++) {
                    projectiles.add(new BulletEntity(level, initialPos, type, pierceLevel, longshotLevel));
                }
                deviation = getDeviation(stack);
            }
            case GOLD -> {
                projectiles.add(0, new BulletEntity(level, initialPos, type, pierceLevel, longshotLevel));
                ListTag listTag = stack.getEnchantmentTags();
                if (!(listTag.size() > 0)) break;

                Map<Enchantment, Integer> enchantments = new HashMap<>(EnchantmentHelper.getEnchantments(stack));
                BulletEntity bulletEntity = projectiles.get(0);
                int ench = 0;

                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
                    ench += getEnchantPower(entry.getKey(), entry.getValue());

                bulletEntity.setMagicBullet(Math.min((ench / 2) + 1, 24));
            }
            default -> projectiles.add(new BulletEntity(level, initialPos, type, pierceLevel, longshotLevel));
        }

        for(BulletEntity bullet : projectiles) {
            bullet.setOwner(player);
            bullet.setPos(initialPos);
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4F, deviation);
            level.addFreshEntity(bullet);
        }
        stack.hurtAndBreak(type == BulletType.NETHERITE ? 3 : 1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
    }

    private int getEnchantPower(Enchantment enchantment, int level) {
        Enchantment.Rarity rarity = enchantment.getRarity();
        return switch (rarity) {
            case UNCOMMON -> 2 * level;
            case RARE -> 3 * level;
            case VERY_RARE -> 4 * level;
            default -> level;
        };
    }
}