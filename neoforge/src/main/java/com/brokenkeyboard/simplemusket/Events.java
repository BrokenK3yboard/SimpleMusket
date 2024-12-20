package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.HatModel;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import com.brokenkeyboard.simplemusket.item.BulletDispenseBehavior;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3f;

import java.util.List;

@SuppressWarnings("unused")
public class Events {

    @EventBusSubscriber(modid = ModRegistry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModRegistry.BULLET_ENTITY, BulletEntityRenderer::new);
            event.registerEntityRenderer(ModRegistry.GUNSLINGER, MusketPillagerRenderer::new);
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            ModRegistry.registerSensorGoal();
            DispenserBlock.registerBehavior(ModRegistry.CARTRIDGE, new BulletDispenseBehavior(ModRegistry.CARTRIDGE));
            DispenserBlock.registerBehavior(ModRegistry.ENCHANTED_CARTRIDGE, new BulletDispenseBehavior(ModRegistry.ENCHANTED_CARTRIDGE));
            DispenserBlock.registerBehavior(ModRegistry.HELLFIRE_CARTRIDGE, new BulletDispenseBehavior(ModRegistry.HELLFIRE_CARTRIDGE));
        }

        @SubscribeEvent
        public static void clientSetup(final FMLClientSetupEvent event) {
            event.enqueueWork(ModRegistry::registerItemProperties);
        }

        @SubscribeEvent
        public static void spawnRegister(RegisterSpawnPlacementsEvent event) {
            event.register(ModRegistry.GUNSLINGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    PatrollingMonster::checkPatrollingMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        }

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC, (data, context) -> {
                String source = data.soundSource();
                Vector3f pos = data.pos();
                context.enqueueWork(() -> ClientPacketHandler.playSound(source, pos));
            });
        }
    }

    @EventBusSubscriber(modid = ModRegistry.MOD_ID)
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
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(ModRegistry.MUSKET), 3, 10, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), new ItemStack(ModRegistry.CARTRIDGE, 4), 16, 1, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(ModRegistry.ENCHANTED_CARTRIDGE, 4), 16, 1, 0.05F));
        }
    }

    @EventBusSubscriber(modid = ModRegistry.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onRenderLivingEventPre(final RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
            if (!(event.getEntity() instanceof Player player)) return;
            InteractionHand hand = player.getUsedItemHand();
            if (player.getItemInHand(hand).getItem() instanceof MusketItem) {
                HumanoidModel<Player> model = event.getRenderer().getModel();
                if (MusketItem.isLoaded(player.getItemInHand(hand))) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    } else {
                        model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    }
                } else if (player.isUsingItem() && !MusketItem.isLoaded(player.getItemInHand(hand))) {
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

    @EventBusSubscriber(modid = ModRegistry.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegisterModels {

        @SubscribeEvent
        public static void armorLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(new ModelLayerLocation(ModRegistry.location("musket_pillager"), "overlay"), HatModel::createBodyLayer);
        }
    }
}