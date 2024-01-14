package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.conditions.CopperCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.GoldCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.NetheriteCondition;
import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.network.PacketHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

import static com.brokenkeyboard.simplemusket.SimpleMusket.MUSKET_PILLAGER;

@SuppressWarnings("unused")
public class Events {

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SimpleMusket.BULLET_ENTITY.get(), BulletEntityRenderer::new);
            event.registerEntityRenderer(MUSKET_PILLAGER.get(), MusketPillagerRenderer::new);
        }

        @SubscribeEvent
        public static void registerSerializers(RegisterEvent event) {
            if (!(event.getRegistryKey() == ForgeRegistries.Keys.RECIPE_SERIALIZERS)) return;
            CraftingHelper.register(CopperCondition.SERIALIZER);
            CraftingHelper.register(GoldCondition.SERIALIZER);
            CraftingHelper.register(NetheriteCondition.SERIALIZER);
        }

        @SubscribeEvent
        public static void setup(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "loading"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && !MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "load_stage"),
                        (stack, world, living, id) -> (living == null || MusketItem.hasAmmo(stack)) ? 0.0F :
                                MusketItem.getReloadPerc(stack, stack.getUseDuration() - living.getUseItemRemainingTicks()));

                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "loaded"),
                        (stack, world, living, id) -> living != null && MusketItem.hasAmmo(stack) ? 1.0F : 0.0F);

                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "aiming"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);
            });
        }

        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(MUSKET_PILLAGER.get(), MusketPillager.createAttributes().build());
        }

        @SubscribeEvent
        public static void spawnRegister(SpawnPlacementRegisterEvent event) {
            event.register(MUSKET_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    PatrollingMonster::checkPatrollingMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
        }
    }

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID)
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
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(SimpleMusket.MUSKET.get()), 3, 10, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(SimpleMusket.COPPER_BULLET.get(), 4), 16, 1, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(SimpleMusket.IRON_BULLET.get(), 4), 16, 1, 0.05F));
            trades.get(3).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(SimpleMusket.GOLD_BULLET.get(),4 ), 16, 1, 0.05F));
        }
    }

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonSetup {

        @SubscribeEvent
        public static void setup(final FMLCommonSetupEvent event) {
            event.enqueueWork(PacketHandler::register);
        }
    }

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
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
}