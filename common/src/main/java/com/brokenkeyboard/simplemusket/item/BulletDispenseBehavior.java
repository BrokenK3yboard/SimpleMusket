package com.brokenkeyboard.simplemusket.item;

import com.brokenkeyboard.simplemusket.platform.Services;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;

public class BulletDispenseBehavior extends ProjectileDispenseBehavior {

    public BulletDispenseBehavior(Item item) {
        super(item);
    }

    @Override
    protected void playSound(BlockSource source) {
        Services.PLATFORM.playSound(SoundSource.NEUTRAL, source.level(), source.center());
    }
}
