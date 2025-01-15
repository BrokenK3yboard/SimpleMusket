package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.Conditions;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.network.S2CSoundPacket;
import fuzs.extensibleenums.api.extensibleenums.v1.BuiltInEnumFactories;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiConsumer;

import static com.brokenkeyboard.simplemusket.ModRegistry.GUNSLINGER;

public class SimpleMusket implements ModInitializer {

    public static final PacketType<S2CSoundPacket> SOUND_TYPE = PacketType.create(new ResourceLocation(Constants.MOD_ID, "sound"), S2CSoundPacket::new);

    @Override
    public void onInitialize() {

        ForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.SPEC);

        ModRegistry.registerEntity(bind(BuiltInRegistries.ENTITY_TYPE));
        ModRegistry.registerItems(bind(BuiltInRegistries.ITEM));
        ModRegistry.registerEnchants(bind(BuiltInRegistries.ENCHANTMENT));
        ModRegistry.registerEffects(bind(BuiltInRegistries.MOB_EFFECT));
        ModRegistry.registerSounds(bind(BuiltInRegistries.SOUND_EVENT));

        ResourceConditions.register(Constants.CRAFT_CARTRIDGE, object -> true);
        ResourceConditions.register(Constants.CRAFT_ENCHANTED, object -> Conditions.enabledRecipe(object, Config.CRAFT_ENCHANTED_CARTRIDGE));
        ResourceConditions.register(Constants.CRAFT_HELLFIRE, object -> Conditions.enabledRecipe(object, Config.CRAFT_HELLFIRE_CARTRIDGE));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.MUSKET));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.ENCHANTED_CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.HELLFIRE_CARTRIDGE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(ModRegistry.GUNSLINGER_EGG));

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET), 3, 10, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE, 4), 16, 1, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 3, factories -> factories.add((entity, random) ->
                new MerchantOffer(new ItemStack(Items.EMERALD, 3), new ItemStack(ModRegistry.ENCHANTED_CARTRIDGE, 4), 16, 1, 0.05F)));

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if ((Constants.BASTION_OTHER.equals(id) || Constants.BASTION_TREASURE.equals(id) || Constants.BASTION_BRIDGE.equals(id))
                    && source.isBuiltin() && Config.FIND_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE).setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))));
            }

            if (Constants.PIGLIN_BARTER.equals(id) && source.isBuiltin() && Config.BARTER_HELLFIRE_CARTRIDGE.get()) {
                tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ModRegistry.HELLFIRE_CARTRIDGE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).setWeight(10)));
            }
        });

        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.HAS_PILLAGER_OUTPOST), MobCategory.MONSTER, GUNSLINGER, 15, 1, 1);
        BuiltInEnumFactories.createRaiderType(ModRegistry.GUNSLINGER.toString(), ModRegistry.GUNSLINGER, new int[] {0, 0, 0, 0, 0, 1, 1, 2});
        FabricDefaultAttributeRegistry.register(ModRegistry.GUNSLINGER, MusketPillager.createAttributes());
        ModRegistry.registerSensorGoal();
        SpawnPlacements.register(ModRegistry.GUNSLINGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PatrollingMonster::checkPatrollingMonsterSpawnRules);

        FabricLoader.getInstance().getObjectShare().put("usefulspyglass:" + ModRegistry.MUSKET, ModRegistry.AIMING_MUSKET);
    }

    private static <T> BiConsumer<ResourceLocation, T> bind(Registry<? super T> registry) {
        return (location, t) -> Registry.register(registry, location, t);
    }
}