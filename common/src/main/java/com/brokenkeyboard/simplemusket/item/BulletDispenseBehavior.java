package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class BulletDispenseBehavior extends ProjectileDispenseBehavior {

    public BulletDispenseBehavior(Item item) {
        super(item);
    }

    @Override
    protected void playSound(BlockSource source) {
        Services.PLATFORM.playSound(SoundSource.BLOCKS, source.level(), source.center());
    }

    @Override
    protected void playAnimation(BlockSource blockSource, Direction direction) {
        MusketItem.spawnParticles(blockSource.level(),
                new Vec3(blockSource.pos().getX(), blockSource.pos().getY(), blockSource.pos().getZ()),
                new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
    }
}
