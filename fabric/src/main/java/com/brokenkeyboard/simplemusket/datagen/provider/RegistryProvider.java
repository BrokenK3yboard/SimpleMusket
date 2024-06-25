package com.brokenkeyboard.simplemusket.datagen.provider;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.datagen.Datagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.ModRegistry.*;

public class RegistryProvider extends FabricDynamicRegistryProvider {

    public RegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        HolderSet.Named<Item> itemHolder = registries.lookupOrThrow(Registries.ITEM).getOrThrow(ModRegistry.MUSKET_ENCHANTABLE);
        HolderGetter<Enchantment> enchHolder = registries.lookupOrThrow(Registries.ENCHANTMENT);

        entries.add(ModRegistry.BULLET, Datagen.BULLET);

        entries.add(ModRegistry.FIREPOWER, Enchantment.enchantment(Enchantment.definition(itemHolder, 10, 5, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.1F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY).build()))
                .build(ModRegistry.FIREPOWER.location()));

        entries.add(ModRegistry.DEADEYE, Enchantment.enchantment(Enchantment.definition(itemHolder, 5, 3, Enchantment.dynamicCost(12, 20), Enchantment.constantCost(50), 2, EquipmentSlotGroup.MAINHAND))
                .build(ModRegistry.DEADEYE.location()));

        entries.add(ModRegistry.LONGSHOT, Enchantment.enchantment(Enchantment.definition(itemHolder, 2, 2, Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), 4, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchHolder.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .build(ModRegistry.LONGSHOT.location()));

        entries.add(ModRegistry.REPEATING, Enchantment.enchantment(Enchantment.definition(itemHolder, 1, 1, Enchantment.constantCost(20), Enchantment.constantCost(50), 8, EquipmentSlotGroup.MAINHAND))
                .build(ModRegistry.REPEATING.location()));

        entries.add(GUNSLINGER_SPAWN_MUSKET, new SingleEnchantment(enchHolder.getOrThrow(ModRegistry.FIREPOWER), ConstantInt.of(1)));
        entries.add(RAID_GUNSLINGER_POST_WAVE_3, new SingleEnchantment(enchHolder.getOrThrow(ModRegistry.FIREPOWER), ConstantInt.of(2)));
        entries.add(RAID_GUNSLINGER_POST_WAVE_5, new SingleEnchantment(enchHolder.getOrThrow(ModRegistry.REPEATING), ConstantInt.of(1)));
    }

    @Override
    public String getName() {
        return "DynamicData";
    }
}
