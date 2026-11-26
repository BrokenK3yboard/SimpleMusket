package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

public class SMLootProvider extends EntityLootSubProvider {

    public SMLootProvider(HolderLookup.Provider provider) {
        super(FeatureFlags.DEFAULT_FLAGS, provider);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return SimpleMusket.ENTITIES.getEntries()
                .stream().map(Holder::value)
                .filter(entityType -> entityType.getCategory() != MobCategory.MISC)
                .map(entityType -> (EntityType<?>) entityType);
    }

    @Override
    public void generate() {
        this.add(ModRegistry.GUNSLINGER.value(),
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModRegistry.CARTRIDGE.value())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
    }
}
