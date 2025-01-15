package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class BulletItem extends Item {

    public final float VELOCITY;

    public BulletItem(float speed) {
        super(new Item.Properties());
        VELOCITY = speed;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {

        protected ItemStack execute(BlockSource source, ItemStack stack) {
            ServerLevel level = source.getLevel();
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Position position = DispenserBlock.getDispensePosition(source);
            Vec3 initialPos = new Vec3(position.x(), position.y(), position.z());
            BulletEntity bullet = new BulletEntity(level, null, initialPos, stack, stack);
            bullet.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), ((BulletItem)bullet.getBullet()).VELOCITY, 0.5F);
            level.addFreshEntity(bullet);
            MusketItem.spawnParticles(level, initialPos, new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            Services.PLATFORM.playSound(ModRegistry.MUSKET_FIRE, SoundSource.BLOCKS, source.getLevel(), source.getPos().getCenter());
        }

        @Override
        protected void playAnimation(BlockSource blockSource, Direction direction) {
            MusketItem.spawnParticles(blockSource.getLevel(),
                    new Vec3(blockSource.x(), blockSource.y(), blockSource.z()),
                    new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
        }
    };
}