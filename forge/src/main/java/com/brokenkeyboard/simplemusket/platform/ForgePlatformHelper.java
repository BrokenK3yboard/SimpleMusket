package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.network.Network;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.ModList;

import static com.brokenkeyboard.simplemusket.SimpleMusket.RECIPE_SERIALIZERS;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean bulletHitResult(BulletEntity bullet, HitResult result) {
        return net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(bullet, result);
    }

    @Override
    public void playSound(SoundEvent sound, SoundSource source, ServerLevel level, Vec3 origin) {
        Network.S2CSound(sound, source, level.dimension(), origin);
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof PartEntity<?> part ? part.getParent() : entity;
    }

    @Override
    public EnchantmentCategory musketCategory() {
        return EnchantmentCategory.create("MUSKET", item -> item instanceof MusketItem);
    }

    @Override
    public EntityType<? extends BulletEntity> createBulletEntity() {
        return EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F).clientTrackingRange(4)
                .updateInterval(20).setShouldReceiveVelocityUpdates(false).build("bullet");
    }

    @Override
    public <T extends CraftingRecipe> RecipeSerializer<T> createRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        RECIPE_SERIALIZERS.register(name, () -> serializer);
        return serializer;
    }
}