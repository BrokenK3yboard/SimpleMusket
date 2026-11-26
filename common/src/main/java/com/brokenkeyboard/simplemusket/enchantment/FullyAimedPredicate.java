package com.brokenkeyboard.simplemusket.enchantment;

import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record FullyAimedPredicate() implements EntitySubPredicate {

    public static final MapCodec<FullyAimedPredicate> CODEC = MapCodec.unit(new FullyAimedPredicate());

    public static final FullyAimedPredicate FULLY_AIMED = new FullyAimedPredicate();

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
        return entity instanceof BulletEntity bulletEntity && bulletEntity.isFullyAimed();
    }
}
