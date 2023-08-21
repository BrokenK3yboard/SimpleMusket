package com.brokenkeyboard.simplemusket.entity;

import com.brokenkeyboard.simplemusket.item.FirearmItem;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.entity.goal.FirearmAttackGoal;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.world.entity.projectile.Projectile;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

public class MusketPillager extends AbstractIllager implements InventoryCarrier {

    private static final EntityDataAccessor<Boolean> RELOADING = SynchedEntityData.defineId(MusketPillager.class, EntityDataSerializers.BOOLEAN);
    private final SimpleContainer inventory = new SimpleContainer(5);

    public MusketPillager(EntityType<? extends MusketPillager> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new FirearmAttackGoal(this, 1.0F, 32));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RELOADING, false);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item.asItem() instanceof FirearmItem;
    }

    public boolean isReloading() {
        return this.entityData.get(RELOADING);
    }

    public void setReloading(boolean value) {
        this.entityData.set(RELOADING, value);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag listtag = new ListTag();

        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                listtag.add(stack.save(new CompoundTag()));
            }
        }
        tag.put("Inventory", listtag);
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isReloading()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(is -> is.getItem() instanceof FirearmItem)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        } else {
            return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag listtag = tag.getList("Inventory", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            ItemStack stack = ItemStack.of(listtag.getCompound(i));
            if (!stack.isEmpty()) {
                this.inventory.addItem(stack);
            }
        }
        this.setCanPickUpLoot(true);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0.0F;
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        this.populateDefaultEquipmentSlots(difficulty);
        this.populateDefaultEquipmentEnchantments(difficulty);
        return super.finalizeSpawn(accessor, difficulty, type, groupData, tag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        ItemStack stack = new ItemStack(SimpleMusket.MUSKET.get());
        FirearmItem.setAmmoType(stack, 1);
        FirearmItem.setReady(stack, true);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    protected void enchantSpawnedWeapon(float value) {
        super.enchantSpawnedWeapon(value);
        if (this.random.nextInt(300) == 0) {
            ItemStack stack = this.getMainHandItem();
            if (stack.getItem() instanceof FirearmItem) {
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
                map.putIfAbsent(SimpleMusket.FIREPOWER.get(), 1);
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

    public void useFirearm(ItemStack stack) {
        FirearmItem firearm = (FirearmItem) stack.getItem();
        firearm.createProjectile(this, level, stack, 0.0F);
        level.playSound(null, getX(), getY(), getZ(), firearm.getFireSound(), SoundSource.HOSTILE, 1F, 1F);
    }

    public void shootBullet(LivingEntity entity, LivingEntity target, Projectile projectile, float speed) {
        Vec3 direction = new Vec3(target.getX() - getX(), target.getBoundingBox().minY + target.getBbHeight() * 0.7f - getY() - getEyeHeight(),
                target.getZ() - getZ()).normalize();
        projectile.shoot(direction.x(), direction.y(), direction.z(), speed, (float)(8 - entity.level.getDifficulty().getId() * 2));
    }

    public Container getInventory() {
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
        if (raid == null) return;
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            ItemStack stack = new ItemStack(SimpleMusket.MUSKET.get());
            Map<Enchantment, Integer> map = Maps.newHashMap();
            if (value > raid.getNumGroups(Difficulty.NORMAL)) {
                map.put(SimpleMusket.FIREPOWER.get(), 2);
            } else if (value > raid.getNumGroups(Difficulty.EASY)) {
                map.put(SimpleMusket.FIREPOWER.get(), 1);
            }

            map.put(SimpleMusket.DEADEYE.get(), 1);
            EnchantmentHelper.setEnchantments(map, stack);
            this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}