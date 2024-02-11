package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.Conditions;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.network.S2CSoundPacket;
import fuzs.extensibleenums.api.extensibleenums.v1.BuiltInEnumFactories;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fml.config.ModConfig;

public class SimpleMusket implements ModInitializer {

    public static final PacketType<S2CSoundPacket> SOUND_TYPE = PacketType.create(new ResourceLocation(Constants.MOD_ID, "sound"), S2CSoundPacket::new);
    public static final SpawnEggItem MUSKET_PILLAGER_EGG = new SpawnEggItem(ModRegistry.MUSKET_PILLAGER.get(), 5258034, 2960169, new Item.Properties());

    @Override
    public void onInitialize() {

        ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.SPEC);

        ResourceConditions.register(Constants.CRAFT_CARTRIDGE, object -> true);
        ResourceConditions.register(Constants.CRAFT_ENCHANTED, object -> false);
        ResourceConditions.register(Constants.CRAFT_HELLFIRE, object -> Conditions.enabledRecipe(object, Config.CRAFT_HELLFIRE_CARTRIDGE));

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, "musket_pillager_spawn_egg"), MUSKET_PILLAGER_EGG);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.MUSKET.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.CARTRIDGE.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.HELLFIRE_CARTRIDGE.get()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(MUSKET_PILLAGER_EGG));

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET.get()), 3, 10, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE.get(), 4), 16, 1, 0.05F)));

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if ((Constants.BASTION_OTHER.equals(id) || Constants.HOGLIN_STABLE.equals(id) || Constants.BASTION_TREASURE.equals(id) ||
                    Constants.BASTION_BRIDGE.equals(id)) && source.isBuiltin() && Config.FIND_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE.get()).setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))));
            }

            if (Constants.PIGLIN_BARTER.equals(id) && source.isBuiltin() && Config.BARTER_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).setWeight(10)));
            }
        });

        FabricDefaultAttributeRegistry.register(ModRegistry.MUSKET_PILLAGER.get(), MusketPillager.createAttributes());

        BuiltInEnumFactories.createRaiderType(ModRegistry.MUSKET_PILLAGER.toString(), ModRegistry.MUSKET_PILLAGER.get(), new int[]{0, 2, 2, 2, 3, 3, 3, 4, 4});
        ModRegistry.registerSensorGoal();

        SpawnPlacements.register(ModRegistry.MUSKET_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PatrollingMonster::checkPatrollingMonsterSpawnRules);
    }
}