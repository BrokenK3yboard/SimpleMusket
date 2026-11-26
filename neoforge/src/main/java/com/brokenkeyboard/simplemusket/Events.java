package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.entity.*;
import com.brokenkeyboard.simplemusket.item.BulletDispenseBehavior;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.item.MusketLoading;
import com.brokenkeyboard.simplemusket.item.MusketState;
import com.brokenkeyboard.simplemusket.network.ClientPacketHandler;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3fc;

@SuppressWarnings("unused")
public class Events {

    @EventBusSubscriber(modid = Constants.MOD_ID)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            ModRegistry.createEntityAttributes((entityType, builder) -> event.put(entityType, builder.build()));
        }

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModRegistry.BULLET_ENTITY.value(), BulletEntityRenderer::new);
            event.registerEntityRenderer(ModRegistry.GUNSLINGER.value(), GunslingerRenderer::new);
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            ModRegistry.registerSensorGoal();
            DispenserBlock.registerBehavior(ModRegistry.CARTRIDGE.value(), new BulletDispenseBehavior(ModRegistry.CARTRIDGE.value()));
        }

        @SubscribeEvent
        public static void spawnRegister(RegisterSpawnPlacementsEvent event) {
            event.register(ModRegistry.GUNSLINGER.value(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    PatrollingMonster::checkPatrollingMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        }

        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC, (payload, context) -> {
                String source = payload.soundSource();
                Vector3fc pos = payload.pos();
                context.enqueueWork(() -> ClientPacketHandler.playSound(source, pos));
            });
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID)
    public static class CommonEvents {

        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            if (event.getEntity() instanceof AbstractVillager villager) {
                villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, Gunslinger.class, 8.0F, 0.6D, 0.6D));
            }
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void registerConditionalProperties(RegisterSelectItemModelPropertyEvent event) {
            event.register(ModRegistry.location("musket_state"), MusketState.TYPE);
        }

        @SubscribeEvent
        public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
            event.register(ModRegistry.location("musket_loading"), MusketLoading.MAP_CODEC);
        }


        @SubscribeEvent
        public static void onRenderLivingEventPre(final RenderLivingEvent.Pre<Player, AvatarRenderState, PlayerModel> event) {
            if (event.getRenderState() instanceof AvatarRenderState renderState) {
                InteractionHand hand = renderState.useItemHand;
                HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
                ItemStack itemStack = renderState.getUseItemStackForArm(arm);

                if (itemStack.getItem() instanceof MusketItem) {
                    if (MusketItem.isLoaded(itemStack)) {
                        if (hand == InteractionHand.MAIN_HAND) {
                            renderState.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                        } else {
                            renderState.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                        }
                    } else if (renderState.ticksUsingItem(arm) > 0 && !MusketItem.isLoaded(itemStack)) {
                        if (hand == InteractionHand.MAIN_HAND) {
                            renderState.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                        } else {
                            renderState.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void armorLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(GunslingerHatLayer.GUNSLINGER_HAT, GunslingerHatModel::createBodyLayer);
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
}