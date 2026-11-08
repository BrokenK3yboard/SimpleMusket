package com.brokenkeyboard.simplemusket.entity.goal;

import com.brokenkeyboard.simplemusket.platform.Services;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class BayonetAttackGoal extends MeleeAttackGoal {

    private static final Multimap<Attribute, AttributeModifier> BAYONET_MELEE_MODIFIER = HashMultimap.create();
    private final float attackRangeSqr;

    static {
        BAYONET_MELEE_MODIFIER.put(Attributes.ATTACK_DAMAGE, new AttributeModifier("bayonet_damage", 4F, AttributeModifier.Operation.ADDITION));
    }

    public BayonetAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, float attackRange) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.attackRangeSqr = attackRange * attackRange;
    }

    @Override
    public void start() {
        super.start();
        Services.PLATFORM.setMobUsingBayonet(mob, true);
        mob.getAttributes().addTransientAttributeModifiers(BAYONET_MELEE_MODIFIER);
    }

    @Override
    public void stop() {
        super.stop();
        Services.PLATFORM.setMobUsingBayonet(mob, false);
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

    @Override
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        double bbWidth = mob.getBbWidth() + 0.5F;
        return bbWidth * 2F * bbWidth * 2F + attackTarget.getBbWidth();
    }

    private boolean isValidTarget() {
        return mob.getTarget() != null && mob.getTarget().distanceToSqr(mob) < attackRangeSqr;
    }
}
