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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Map;

import static com.brokenkeyboard.simplemusket.Config.GUNSLINGER_RANGE;

public class MusketPillager extends AbstractIllager implements InventoryCarrier {

    private static final EntityDataAccessor<Boolean> RELOADING = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SAWN_OFF = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private final SimpleContainer inventory = new SimpleContainer(5);
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
        this.goalSelector.addGoal(4, new MusketAttackGoal(this, 1.0F, GUNSLINGER_RANGE.get().floatValue()));
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
                .add(Attributes.FOLLOW_RANGE, GUNSLINGER_RANGE.get().floatValue() + 16F)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RELOADING, false);
        this.entityData.define(SAWN_OFF, false);
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
    public void aiStep() {
        super.aiStep();
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
        this.writeInventoryToTag(tag);
        tag.putInt("Sawnoff_cooldown", sawnoffCooldown);
        tag.putInt("Attack_cooldown", sawnoffCooldown);
    }

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
        this.readInventoryFromTag(tag);
        this.setCanPickUpLoot(true);
        sawnoffCooldown = tag.getInt("Sawnoff_cooldown");
        attackCooldown = tag.getInt("Attack_cooldown");
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0.0F;
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Nullable
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
        MusketItem.setLoaded(stack, true);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

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
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PILLAGER_HURT;
    }

    public void useMusket(ItemStack stack) {
        MusketItem musket = (MusketItem) stack.getItem();
        int deadeye = EnchantmentHelper.getItemEnchantmentLevel(ModRegistry.DEADEYE, stack);
        double deviation = (float) (8 - level().getDifficulty().getId() * 2);
        musket.fire(this, level(), SoundSource.HOSTILE, stack, deadeye > 0 ? deviation * (1 - (0.125 + 0.125 * deviation)) : deviation);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    protected void pickUpItem(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        if (stack.getItem() instanceof BannerItem) {
            super.pickUpItem(entity);
        } else if (this.wantsItem(stack)) {
            this.onItemPickup(entity);
            ItemStack stack1 = this.inventory.addItem(stack);
            if (stack1.isEmpty()) {
                entity.discard();
            } else {
                stack.setCount(stack1.getCount());
            }
        }
    }

    private boolean wantsItem(ItemStack stack) {
        return this.hasActiveRaid() && stack.is(Items.WHITE_BANNER);
    }

    public SlotAccess getSlot(int value) {
        int i = value - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(value);
    }

    @Override
    public void applyRaidBuffs(int value, boolean bool) {
        Raid raid = this.getCurrentRaid();
        if (raid == null || this.random.nextFloat() > raid.getEnchantOdds()) return;
        ItemStack stack = new ItemStack(ModRegistry.MUSKET);
        Map<Enchantment, Integer> map = Maps.newHashMap();

        if (value > raid.getNumGroups(Difficulty.HARD)) {
            map.put(ModRegistry.REPEATING, 1);
            map.put(ModRegistry.FIREPOWER, 2);
        } else if (value > raid.getNumGroups(Difficulty.NORMAL)) {
            map.put(ModRegistry.FIREPOWER, 2);
        } else if (value > raid.getNumGroups(Difficulty.EASY)) {
            map.put(ModRegistry.FIREPOWER, 1);
        }

        map.put(ModRegistry.DEADEYE, 1);
        EnchantmentHelper.setEnchantments(map, stack);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}