package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class BulletItem extends Item implements ProjectileItem {

    public BulletItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new BulletEntity(level, null, pos.x(), pos.y(), pos.z(), null);
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder().uncertainty(0.5F).power(Constants.BULLET_VELOCITY).build();
    }
}