package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.entity.goal.MusketAttackGoal;
import com.brokenkeyboard.simplemusket.entity.goal.SawnOffGoal;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Optional;

public class MusketPillager extends AbstractIllager {

    private static final EntityDataAccessor<Boolean> RELOADING = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SAWN_OFF = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private int sawnoffCooldown = 0;
    private int attackCooldown = 0;

    public MusketPillager(EntityType<? extends MusketPillager> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SawnOffGoal(this, 8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 12, 1.0, 1.2));
        this.goalSelector.addGoal(3, new HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(4, new MusketAttackGoal(this, 1.0F, 40F));
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
                .add(Attributes.FOLLOW_RANGE, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RELOADING, false);
        builder.define(SAWN_OFF, false);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item.asItem() instanceof MusketItem;
    }

    public boolean isReloading() {
        return this.entityData.get(RELOADING);
    }

    public void setReloading(boolean value) {
        this.entityData.set(RELOADING, value);
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

    public int getAttackCD() {
        return attackCooldown;
    }

    public void setAttackCD(int value) {
        attackCooldown = value;
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        if (sawnoffCooldown > 0) {
            --sawnoffCooldown;
        }
        if (attackCooldown > 0) {
            --attackCooldown;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Sawnoff_cooldown", sawnoffCooldown);
        tag.putInt("Attack_cooldown", sawnoffCooldown);
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isReloading()) {
            return IllagerArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(is -> is.getItem() instanceof MusketItem)) {
            return isUsingSawnOff() ? IllagerArmPose.ATTACKING : IllagerArmPose.CROSSBOW_HOLD;
        } else {
            return this.isAggressive() ? IllagerArmPose.ATTACKING : IllagerArmPose.NEUTRAL;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setCanPickUpLoot(true);
        sawnoffCooldown = tag.getInt("Sawnoff_cooldown");
        attackCooldown = tag.getInt("Attack_cooldown");
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data) {
        RandomSource randomsource = accessor.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(accessor, randomsource, difficulty);
        return super.finalizeSpawn(accessor, difficulty, type, data);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        ItemStack stack = new ItemStack(ModRegistry.MUSKET);
        MusketItem.setAmmo(stack, new ItemStack(ModRegistry.CARTRIDGE));
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
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

    public void useMusket(ItemStack stack) {
        MusketItem musket = (MusketItem) stack.getItem();
        Optional<Registry<Enchantment>> registry = level().registryAccess().registry(Registries.ENCHANTMENT);
        float deviation = (float) (8 - level().getDifficulty().getId() * 2);
        int deadeye = registry.map(enchantments -> EnchantmentHelper.getItemEnchantmentLevel(enchantments.getHolderOrThrow(ModRegistry.DEADEYE), stack)).orElse(0);
        musket.fire(level(), this, this.getUsedItemHand(), stack, 4F, (deadeye > 0 ? (float) (deviation * (1 - (0.125 + 0.125 * deviation))) : deviation), this.getTarget(), SoundSource.HOSTILE);
    }

    @Override
    public void applyRaidBuffs(ServerLevel serverLevel, int value, boolean bool) {
        Raid raid = this.getCurrentRaid();
        if (raid == null) return;
        boolean enchantWeapon = this.random.nextFloat() <= raid.getEnchantOdds();
        if (enchantWeapon) {
            ItemStack stack = new ItemStack(ModRegistry.MUSKET);
            ResourceKey<EnchantmentProvider> key;
            if (value > raid.getNumGroups(Difficulty.NORMAL)) {
                key = ModRegistry.RAID_GUNSLINGER_POST_WAVE_5;
            } else if (value > raid.getNumGroups(Difficulty.EASY)) {
                key = ModRegistry.RAID_GUNSLINGER_POST_WAVE_3;
            } else {
                key = null;
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