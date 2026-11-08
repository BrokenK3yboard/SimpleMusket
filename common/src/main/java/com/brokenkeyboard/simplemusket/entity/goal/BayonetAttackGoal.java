package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class BayonetAttackGoal extends MeleeAttackGoal {

    private final float ATTACK_RANGE_SQR;
    private static final Multimap<Holder<Attribute>, AttributeModifier> BAYONET_MELEE_MODIFIER = HashMultimap.create();

    static {
        BAYONET_MELEE_MODIFIER.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ModRegistry.location("bayonet_damage"), 4F, AttributeModifier.Operation.ADD_VALUE));
    }

    public BayonetAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, float attackRange) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.ATTACK_RANGE_SQR = attackRange * attackRange;
    }

    @Override
    public void start() {
        super.start();
        Services.PLATFORM.setMobUsingBayonet(this.mob, true);
        mob.getAttributes().addTransientAttributeModifiers(BAYONET_MELEE_MODIFIER);
    }

    @Override
    public void stop() {
        super.stop();
        Services.PLATFORM.setMobUsingBayonet(this.mob, false);
        mob.getAttributes().removeAttributeModifiers(BAYONET_MELEE_MODIFIER);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && isValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && isValidTarget();
    }

    private boolean isValidTarget() {
        return mob.getTarget() != null && mob.getTarget().distanceToSqr(mob) < ATTACK_RANGE_SQR;
    }
}
