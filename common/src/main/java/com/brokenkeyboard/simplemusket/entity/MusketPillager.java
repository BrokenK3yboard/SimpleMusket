package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.goal.MusketAttackGoal;
import com.brokenkeyboard.simplemusket.entity.goal.SawnOffGoal;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;

public class MusketPillager extends AbstractIllager {

    private static final EntityDataAccessor<Boolean> SAWN_OFF = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private int sawnoffCooldown = 0;

    public MusketPillager(EntityType<? extends MusketPillager> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SawnOffGoal(this, 8.0F));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.0F, 1.2F));
        this.goalSelector.addGoal(3, new HoldGroundAttackGoal(this, 8.0F));
        this.goalSelector.addGoal(4, new MusketAttackGoal<>(this, 1.0F, 24.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAWN_OFF, false);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item.asItem() instanceof MusketItem;
    }

    public boolean isUsingSawnOff() {
        return this.entityData.get(SAWN_OFF);
    }

    public void setUsingSawnOff(boolean value) {
        this.entityData.set(SAWN_OFF, value);
    }

    public int getSawnoffCD() {
        return sawnoffCooldown;
    }

    public void setSawnoffCD(int value) {
        sawnoffCooldown = value;
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        if (sawnoffCooldown > 0) {
            --sawnoffCooldown;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Sawnoff_cooldown", sawnoffCooldown);
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isHolding(stack -> stack.getItem() instanceof MusketItem)) {
            return this.isUsingItem() ? IllagerArmPose.CROSSBOW_CHARGE : (isUsingSawnOff() ? IllagerArmPose.ATTACKING : IllagerArmPose.CROSSBOW_HOLD);
        }
        return this.isAggressive() ? IllagerArmPose.ATTACKING : IllagerArmPose.NEUTRAL;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setCanPickUpLoot(true);
        sawnoffCooldown = tag.getInt("Sawnoff_cooldown");
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0.0F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        RandomSource randomsource = accessor.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);
        return super.finalizeSpawn(accessor, difficulty, type, groupData, tag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        ItemStack stack = new ItemStack(ModRegistry.MUSKET);
        MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE));
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    @Override
    protected void enchantSpawnedWeapon(RandomSource randomSource, float value) {
        super.enchantSpawnedWeapon(randomSource, value);
        if (this.random.nextInt(300) == 0) {
            ItemStack stack = this.getMainHandItem();
            if (stack.getItem() instanceof MusketItem) {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
                map.putIfAbsent(ModRegistry.FIREPOWER, 1);
                EnchantmentHelper.setEnchantments(map, stack);
                this.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
        }
    }

    @Override
    public boolean isAlliedTo(Entity $$0) {
        if (super.isAlliedTo($$0)) {
            return true;
        } else if ($$0 instanceof LivingEntity && ((LivingEntity)$$0).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && $$0.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    public void applyRaidBuffs(int value, boolean bool) {
        Raid raid = this.getCurrentRaid();
        if (raid == null || this.random.nextFloat() > raid.getEnchantOdds()) return;
        ItemStack stack = new ItemStack(ModRegistry.MUSKET);
        Map<Enchantment, Integer> map = Maps.newHashMap();

        if (value > raid.getNumGroups(Difficulty.NORMAL)) {
            map.put(ModRegistry.LONGSHOT, 1);
        } else if (value > raid.getNumGroups(Difficulty.EASY)) {
            map.put(ModRegistry.FIREPOWER, 1);
        }

        EnchantmentHelper.setEnchantments(map, stack);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}