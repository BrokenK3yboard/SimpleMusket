package com.brokenkeyboard.simplemusket.platform;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.platform.services.IPlatformHelper;
import fuzs.extensibleenums.api.extensibleenums.v1.BuiltInEnumFactories;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean bulletHitResult(BulletEntity bullet, HitResult result) {
        return false;
    }

    @Override
    public void playSound(SoundEvent sound, SoundSource source, ServerLevel level, Vec3 origin) {
        for (ServerPlayer serverPlayer : PlayerLookup.around(level, origin, 64)) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            sound.writeToNetwork(buf);
            buf.writeEnum(source);
            buf.writeVector3f(origin.toVector3f());
            ServerPlayNetworking.send(serverPlayer, SimpleMusket.SOUND_TYPE.getId(), buf);
        }
    }

    @Override
    public Entity getHitEntity(Entity entity) {
        return entity instanceof EnderDragonPart part ? part.parentMob : entity;
    }

    @Override
    public EnchantmentCategory musketCategory() {
        return BuiltInEnumFactories.createEnchantmentCategory("MUSKET", item -> item instanceof MusketItem);
    }

    @Override
    public EntityType<? extends BulletEntity> createBulletEntity() {
        return FabricEntityTypeBuilder.<BulletEntity>create(MobCategory.MISC)
                .dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeBlocks(4).trackedUpdateRate(20)
                .forceTrackedVelocityUpdates(false).build();
    }

    @Override
    public <T extends CraftingRecipe> RecipeSerializer<T> createRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constants.MOD_ID, name), serializer);
    }
}