package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.conditions.CopperCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.GoldCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.NetheriteCondition;
import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class Events {

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SimpleMusket.BULLET_ENTITY.get(), BulletEntityRenderer::new);
            event.registerEntityRenderer(SimpleMusket.MUSKET_PILLAGER.get(), MusketPillagerRenderer::new);
        }

        @SubscribeEvent
        public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
            if (!(event.getRegistry().getRegistryKey() == ForgeRegistries.Keys.RECIPE_SERIALIZERS)) return;
            CraftingHelper.register(CopperCondition.SERIALIZER);
            CraftingHelper.register(GoldCondition.SERIALIZER);
            CraftingHelper.register(NetheriteCondition.SERIALIZER);
        }

        @SubscribeEvent
        public static void setup(final FMLClientSetupEvent event) {
            event.enqueueWork(() ->
            {
                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "loading"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && !MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "loaded"),
                        (stack, world, living, id) -> living != null && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

                ItemProperties.register(SimpleMusket.MUSKET.get(),
                        new ResourceLocation(SimpleMusket.MOD_ID, "aiming"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && MusketItem.isReady(stack) ? 1.0F : 0.0F);
            });
        }

        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(SimpleMusket.MUSKET_PILLAGER.get(), MusketPillager.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID)
    public static class CommonEvents {

        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof Mob mob) {
                if (mob instanceof AbstractVillager villager) {
                    villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, MusketPillager.class, 8.0F, 0.6D, 0.6D));
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onRenderLivingEventPre(final RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
            if (!(event.getEntity() instanceof Player player)) return;
            InteractionHand hand = player.getUsedItemHand();
            if (player.getItemInHand(hand).getItem() instanceof MusketItem && player.isUsingItem() && !MusketItem.isLoaded(player.getItemInHand(hand))) {
                HumanoidModel<Player> model = event.getRenderer().getModel();
                if (hand == InteractionHand.MAIN_HAND) {
                    model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                } else {
                    model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
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