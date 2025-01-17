package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.datagen.provider.*;
import com.brokenkeyboard.simplemusket.enchantment.AmmoCountEffect;
import com.brokenkeyboard.simplemusket.enchantment.ComponentKillEffect;
import com.brokenkeyboard.simplemusket.enchantment.DamageDistanceEffect;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import static com.brokenkeyboard.simplemusket.ModRegistry.*;

public class Datagen implements DataGeneratorEntrypoint {

    public static final DamageType BULLET = new DamageType("bullet", DamageScaling.NEVER, 0.5F);

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        final FabricDataGenerator.Pack pack = generator.createPack();
        BlockProvider blockTags = pack.addProvider(BlockProvider::new);
        pack.addProvider(RegistryProvider::new);
        pack.addProvider(DamageProvider::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(EntityProvider::new);
        pack.addProvider((output, registriesFuture) -> new ItemProvider(output, registriesFuture, blockTags.contentsGetter()));
        pack.addProvider(EnchantProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.DAMAGE_TYPE, Datagen::damageTypeBC);
        registryBuilder.add(Registries.ENCHANTMENT, Datagen::enchantmentBC);
        registryBuilder.add(Registries.ENCHANTMENT_PROVIDER, Datagen::enchantProviderBC);
    }

    protected static void damageTypeBC(BootstrapContext<DamageType> context) {
        context.register(ModRegistry.BULLET, BULLET);
    }

    protected static void enchantmentBC(BootstrapContext<Enchantment> context) {
        HolderSet.Named<Item> itemHolder = context.lookup(Registries.ITEM).getOrThrow(MUSKET_ENCHANTABLE);
        HolderGetter<Enchantment> enchHolder = context.lookup(Registries.ENCHANTMENT);

        context.register(FIREPOWER, Enchantment.enchantment(Enchantment.definition(itemHolder, 10, 5, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.1F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY).build()))
                .build(FIREPOWER.location()));

        context.register(LONGSHOT, Enchantment.enchantment(Enchantment.definition(itemHolder, 2, 2, Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), 4, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(DAMAGE_DISTANCE, new DamageDistanceEffect(0.15F, LevelBasedValue.perLevel(1.0F, 0.66F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY).build()))
                .build(LONGSHOT.location()));

        context.register(REPEATING, Enchantment.enchantment(Enchantment.definition(itemHolder, 1, 1, Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(MUSKET_EXCLUSIVE))
                .withEffect(AMMO_COUNT, new AmmoCountEffect(LevelBasedValue.constant(1F), LevelBasedValue.constant(1F)))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.VICTIM, new ComponentKillEffect())
                .build(REPEATING.location()));
    }

    protected static void enchantProviderBC(BootstrapContext<EnchantmentProvider> context) {
        HolderGetter<Enchantment> holder = context.lookup(Registries.ENCHANTMENT);
        context.register(GUNSLINGER_SPAWN_MUSKET, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(RAID_GUNSLINGER_POST_WAVE_3, new SingleEnchantment(holder.getOrThrow(FIREPOWER), ConstantInt.of(1)));
        context.register(RAID_GUNSLINGER_POST_WAVE_5, new SingleEnchantment(holder.getOrThrow(LONGSHOT), ConstantInt.of(1)));
    }
}