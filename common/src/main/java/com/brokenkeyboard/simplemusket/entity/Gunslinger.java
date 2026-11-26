package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.goal.MusketAttackGoal;
import com.brokenkeyboard.simplemusket.entity.goal.SawnOffAttackGoal;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Gunslinger extends AbstractIllager {

    private static final EntityDataAccessor<Boolean> SAWN_OFF = SynchedEntityData.defineId(Gunslinger.class, EntityDataSerializers.BOOLEAN);
    private int sawnoffCooldown = 0;

    public Gunslinger(EntityType<? extends Gunslinger> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SawnOffAttackGoal(this, 8F));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8F, 1F, 1.2F));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, IronGolem.class, 8F, 1F, 1.2F));
        this.goalSelector.addGoal(3, new HoldGroundAttackGoal(this, 8F));
        this.goalSelector.addGoal(4, new MusketAttackGoal<>(this, 1F, 24F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 15F, 1F));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Mob.class, 15F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24)
                .add(Attributes.ATTACK_DAMAGE, 5)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SAWN_OFF, false);
    }

    @Override
    public boolean canUseNonMeleeWeapon(ItemStack item) {
        return item.is(ModRegistry.MUSKET.value());
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
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (sawnoffCooldown > 0) {
            --sawnoffCooldown;
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Sawnoff_cooldown", sawnoffCooldown);
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isHolding(stack -> stack.getItem() instanceof MusketItem)) {
            return this.isUsingItem() ? IllagerArmPose.CROSSBOW_CHARGE : (isUsingSawnOff() ? IllagerArmPose.ATTACKING : IllagerArmPose.CROSSBOW_HOLD);
        }
        return this.isAggressive() ? IllagerArmPose.ATTACKING : IllagerArmPose.NEUTRAL;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setCanPickUpLoot(true);
        sawnoffCooldown = input.getIntOr("Sawnoff_cooldown", 0);
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
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData data) {
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
        return super.finalizeSpawn(level, difficulty, spawnReason, data);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        ItemStack stack = new ItemStack(ModRegistry.MUSKET.value());
        MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE.value()));
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    @Override
    protected void enchantSpawnedWeapon(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty) {
        super.enchantSpawnedWeapon(level, random, difficulty);
        if (random.nextInt(300) == 0) {
            ItemStack stack = this.getMainHandItem();
            if (stack.is(ModRegistry.MUSKET.value())) {
                EnchantmentHelper.enchantItemFromProvider(stack, level.registryAccess(), ModRegistry.GUNSLINGER_SPAWN_MUSKET, difficulty, random);
            }
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
    public void applyRaidBuffs(ServerLevel serverLevel, int value, boolean bool) {
        Raid raid = this.getCurrentRaid();
        if (raid == null) return;
        boolean enchantWeapon = this.random.nextFloat() <= raid.getEnchantOdds();

        if (enchantWeapon) {
            ItemStack stack = new ItemStack(ModRegistry.MUSKET.value());
            ResourceKey<EnchantmentProvider> key = null;

            if (value > raid.getNumGroups(Difficulty.NORMAL)) {
                key = ModRegistry.RAID_GUNSLINGER_POST_WAVE_5;
            } else if (value > raid.getNumGroups(Difficulty.EASY)) {
                key = ModRegistry.RAID_GUNSLINGER_POST_WAVE_3;
            }

            if (key != null) {
                EnchantmentHelper.enchantItemFromProvider(stack, serverLevel.registryAccess(), key, serverLevel.getCurrentDifficultyAt(this.blockPosition()), this.getRandom());
                this.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}