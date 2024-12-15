package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BulletItem extends Item implements ProjectileItem {

    public final float VELOCITY;

    public BulletItem(float speed) {
        super(new Item.Properties());
        VELOCITY = speed;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        Vec3 initialPos = new Vec3(pos.x(), pos.y(), pos.z());
        return new BulletEntity(level, null, initialPos, stack.copyWithCount(1), null);
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder().uncertainty(0.5F).power(VELOCITY).build();
    }
}