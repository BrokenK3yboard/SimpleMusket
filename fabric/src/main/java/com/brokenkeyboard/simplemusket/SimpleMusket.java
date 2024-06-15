package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.Conditions;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.fml.config.ModConfig;

import java.util.function.BiConsumer;

import static com.brokenkeyboard.simplemusket.ModRegistry.MUSKET_PILLAGER_EGG;

public class SimpleMusket implements ModInitializer {

    @Override
    public void onInitialize() {

        // ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.SPEC);

        ModRegistry.registerEntity(bind(BuiltInRegistries.ENTITY_TYPE));
        ModRegistry.registerItems(bind(BuiltInRegistries.ITEM));
        ModRegistry.registerEffects(bind(BuiltInRegistries.MOB_EFFECT));
        ModRegistry.registerSounds(bind(BuiltInRegistries.SOUND_EVENT));

        /*
        ResourceConditions.register(Constants.CRAFT_CARTRIDGE, object -> true);
        ResourceConditions.register(Constants.CRAFT_ENCHANTED, object -> false);
        ResourceConditions.register(Constants.CRAFT_HELLFIRE, object -> Conditions.enabledRecipe(object, Config.CRAFT_HELLFIRE_CARTRIDGE));
        */

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.MUSKET));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.HELLFIRE_CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(MUSKET_PILLAGER_EGG));

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET), 3, 10, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemCost(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE, 4), 16, 1, 0.05F)));

        /*
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if ((Constants.BASTION_OTHER.equals(id) || Constants.HOGLIN_STABLE.equals(id) || Constants.BASTION_TREASURE.equals(id) ||
                    Constants.BASTION_BRIDGE.equals(id)) && source.isBuiltin() && Config.FIND_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE).setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))));
            }

            if (Constants.PIGLIN_BARTER.equals(id) && source.isBuiltin() && Config.BARTER_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).setWeight(10)));
            }
        });
         */

        PayloadTypeRegistry.playC2S().register(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC);

        // BuiltInEnumFactories.createRaiderType(ModRegistry.MUSKET_PILLAGER.toString(), ModRegistry.MUSKET_PILLAGER, new int[]{0, 2, 2, 2, 3, 3, 3, 4, 4});
        FabricDefaultAttributeRegistry.register(ModRegistry.MUSKET_PILLAGER, MusketPillager.createAttributes());
        ModRegistry.registerSensorGoal();
        // SpawnPlacements.register(ModRegistry.MUSKET_PILLAGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
    }

    private static <T> BiConsumer<ResourceLocation, T> bind(Registry<? super T> registry) {
        return (location, t) -> Registry.register(registry, location, t);
    }
}