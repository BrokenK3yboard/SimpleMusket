package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import fuzs.extensibleenums.api.extensibleenums.v1.BuiltInEnumFactories;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.fml.config.ModConfig;

import java.util.function.BiConsumer;

import static com.brokenkeyboard.simplemusket.Constants.*;
import static com.brokenkeyboard.simplemusket.ModRegistry.MUSKET_PILLAGER_EGG;

public class SimpleMusket implements ModInitializer {

    @Override
    public void onInitialize() {

        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, Config.SPEC);

        ModRegistry.registerEntity(bind(BuiltInRegistries.ENTITY_TYPE));
        ModRegistry.registerItems(bind(BuiltInRegistries.ITEM));
        ModRegistry.registerEffects(bind(BuiltInRegistries.MOB_EFFECT));
        ModRegistry.registerSounds(bind(BuiltInRegistries.SOUND_EVENT));
        ModRegistry.createEntityAttributes(FabricDefaultAttributeRegistry::register);

        BuiltInEnumFactories.createRaiderType(ModRegistry.MUSKET_PILLAGER.toString(), ModRegistry.MUSKET_PILLAGER, new int[] {0, 1, 1, 1, 2, 2, 2, 2, 3});
        SpawnPlacements.register(ModRegistry.MUSKET_PILLAGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        ModRegistry.registerSensorGoal();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.MUSKET));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.HELLFIRE_CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.ENCHANTED_CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(MUSKET_PILLAGER_EGG));

        PayloadTypeRegistry.playS2C().register(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC);

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET), 3, 10, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemCost(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE, 4), 16, 1, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(ModRegistry.ENCHANTED_CARTRIDGE, 4), 16, 1, 0.05F)));

        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                if (key.location().equals(BASTION_OTHER) || key.location().equals(BASTION_TREASURE) || key.location().equals(BASTION_BRIDGE)) {
                    tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE).setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 11)))));
                }
                if (key.location().equals(PIGLIN_BARTER)) {
                    tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 3))).setWeight(10)));
                }
            }
        });
    }

    private static <T> BiConsumer<ResourceLocation, T> bind(Registry<? super T> registry) {
        return (location, t) -> Registry.register(registry, location, t);
    }
}