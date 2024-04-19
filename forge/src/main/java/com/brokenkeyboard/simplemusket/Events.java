package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.conditions.EnchantedCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.HellfireCondition;
import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.HatModel;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

@SuppressWarnings("unused")
public class Events {

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegisterEvent(final RegisterEvent event) {
            event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
                CraftingHelper.register(EnchantedCondition.SERIALIZER);
                CraftingHelper.register(HellfireCondition.SERIALIZER);
            });
        }

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModRegistry.BULLET_ENTITY, BulletEntityRenderer::new);
            event.registerEntityRenderer(ModRegistry.MUSKET_PILLAGER, MusketPillagerRenderer::new);
        }

        @SubscribeEvent
        public static void setup(final FMLClientSetupEvent event) {
            event.enqueueWork(ModRegistry::registerItemProperties);
        }

        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(ModRegistry.MUSKET_PILLAGER, MusketPillager.createAttributes().build());
        }

        @SubscribeEvent
        public static void spawnRegister(SpawnPlacementRegisterEvent event) {
            event.register(ModRegistry.MUSKET_PILLAGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    PatrollingMonster::checkPatrollingMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID)
    public static class CommonEvents {

        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            if (event.getEntity() instanceof AbstractVillager villager) {
                villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, MusketPillager.class, 8.0F, 0.6D, 0.6D));
            }
        }

        @SubscribeEvent
        public static void addTrades(VillagerTradesEvent event) {
            if(event.getType() != VillagerProfession.WEAPONSMITH) return;
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET), 3, 10, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE, 4), 16, 1, 0.05F));
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onMovementUpdate(MovementInputUpdateEvent event) {
            Player player = event.getEntity();
            ItemStack stack = player.getItemInHand(event.getEntity().getUsedItemHand());

            if (stack.getItem() instanceof MusketItem && MusketItem.isLoaded(stack) && player.isUsingItem()) {
                int deadeye = EnchantmentHelper.getTagEnchantmentLevel(ModRegistry.DEADEYE, stack);
                if (deadeye > 0) {
                    float multiplier = 2 + deadeye;
                    event.getInput().leftImpulse *= multiplier;
                    event.getInput().forwardImpulse *= multiplier;
                }
            }
        }

        @SubscribeEvent
        public static void onRenderLivingEventPre(final RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
            if (!(event.getEntity() instanceof Player player)) return;
            InteractionHand hand = player.getUsedItemHand();
            if (player.getItemInHand(hand).getItem() instanceof MusketItem) {
                HumanoidModel<Player> model = event.getRenderer().getModel();
                if (MusketItem.hasAmmo(player.getItemInHand(hand))) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    } else {
                        model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    }
                } else if (player.isUsingItem() && !MusketItem.hasAmmo(player.getItemInHand(hand))) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                    } else {
                        model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                    }
                }
            }
        }

        @SubscribeEvent
        public static void renderHandEvent(RenderHandEvent event) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            InteractionHand hand = player.getUsedItemHand();
            if (player.getItemInHand(hand).getItem() instanceof MusketItem) {
                if (event.getHand() == InteractionHand.OFF_HAND && hand == InteractionHand.MAIN_HAND && player.isUsingItem()) {
                    event.setCanceled(true);
                } else if (event.getHand() == InteractionHand.MAIN_HAND && hand == InteractionHand.OFF_HAND && player.isUsingItem()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class RegisterModels {

        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional event) {
            event.register(new ModelResourceLocation(Constants.MOD_ID, "musket_inventory", "inventory"));
        }

        @SubscribeEvent
        public static void armorLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ModRegistry.GUNSLINGER_HAT, HatModel::createBodyLayer);
        }
    }
}