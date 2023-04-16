package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.datagen.ModLoot.ModLootSerializer;
import com.brokenkeyboard.simplemusket.datagen.conditions.CopperCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.GoldCondition;
import com.brokenkeyboard.simplemusket.datagen.conditions.NetheriteCondition;
import com.brokenkeyboard.simplemusket.enchantment.DeadeyeEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.FirepowerEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.LongshotEnchantment;
import com.brokenkeyboard.simplemusket.enchantment.RepeatingEnchantment;
import com.brokenkeyboard.simplemusket.entity.BulletEntity;
import com.brokenkeyboard.simplemusket.entity.BulletEntityRenderer;
import com.brokenkeyboard.simplemusket.entity.MusketPillager;
import com.brokenkeyboard.simplemusket.entity.MusketPillagerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

@Mod(SimpleMusket.MOD_ID)
public class SimpleMusket
{
    public static final String MOD_ID = "simplemusket";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final EnchantmentCategory FIREARM = EnchantmentCategory.create("FIREARM", item -> item instanceof MusketItem);

    public static final RegistryObject<EntityType<BulletEntity>> BULLET_ENTITY = ENTITIES.register("bullet_entity", () -> EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(5)
            .build("bullet_entity"));

    public static final RegistryObject<EntityType<MusketPillager>> MUSKET_PILLAGER = ENTITIES.register("musket_pillager", () -> EntityType.Builder.of(MusketPillager::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .canSpawnFarFromPlayer()
            .clientTrackingRange(8)
            .build("musket_pillager"));

    public static final RegistryObject<ModLootSerializer> MODLOOT = GLM.register("musket_mod_loot", ModLootSerializer::new);
    public static final RegistryObject<MusketItem> MUSKET = ITEMS.register("musket", () -> new MusketItem(new net.minecraft.world.item.Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_BULLET = ITEMS.register("iron_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 1));
    public static final RegistryObject<Item> COPPER_BULLET = ITEMS.register("copper_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 2));
    public static final RegistryObject<Item> GOLD_BULLET = ITEMS.register("gold_bullet", () -> new BulletItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 3));
    public static final RegistryObject<Item> NETHERITE_BULLET = ITEMS.register("netherite_bullet", () -> new BulletItem(new net.minecraft.world.item.Item.Properties().tab(CreativeModeTab.TAB_COMBAT), 4));

    public static final RegistryObject<Enchantment> FIREPOWER = ENCHANTMENTS.register("firepower", () -> new FirepowerEnchantment(
            Enchantment.Rarity.COMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> DEADEYE = ENCHANTMENTS.register("deadeye", () -> new DeadeyeEnchantment(
            Enchantment.Rarity.UNCOMMON, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> LONGSHOT = ENCHANTMENTS.register("longshot", () -> new LongshotEnchantment(
            Enchantment.Rarity.RARE, FIREARM, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> REPEATING = ENCHANTMENTS.register("repeating", () -> new RepeatingEnchantment(
            Enchantment.Rarity.VERY_RARE, FIREARM, EquipmentSlot.MAINHAND));

    public SimpleMusket() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.registerConfig();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        ENCHANTMENTS.register(bus);
        GLM.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        if (ModList.get().isLoaded("consecration"))
            bus.addListener(this::enqueueIMC);
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("consecration", "holy_attack", ()
                -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
            if(damageSource.getDirectEntity() != null && damageSource.getDirectEntity() instanceof BulletEntity bullet && Config.CONSECRATION_COMPAT.get()) {
                return bullet.isMagicBullet();
            }
            return false;
        });
    }

    public void setup(final FMLCommonSetupEvent event) {
        int[] intArray = new int[9];
        for (int i = 0; i < 9; i++) {
            intArray[i] = i < 4 ? 2 : 3;
        }
        SpawnPlacements.register(MUSKET_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        Raid.RaiderType.create(MUSKET_PILLAGER.get().toString(), MUSKET_PILLAGER.get(), intArray);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(BULLET_ENTITY.get(), BulletEntityRenderer::new);
            event.registerEntityRenderer(MUSKET_PILLAGER.get(), MusketPillagerRenderer::new);
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
                ItemProperties.register(MUSKET.get(),
                        new ResourceLocation(MOD_ID, "loading"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && !MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

                ItemProperties.register(MUSKET.get(),
                        new ResourceLocation(MOD_ID, "loaded"),
                        (stack, world, living, id) -> living != null && MusketItem.isLoaded(stack) ? 1.0F : 0.0F);

                ItemProperties.register(MUSKET.get(),
                        new ResourceLocation(MOD_ID, "aiming"),
                        (stack, world, living, id) -> living != null && living.getUseItem() == stack && living.isUsingItem()
                                && MusketItem.isReady(stack) ? 1.0F : 0.0F);
            });
        }

        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(MUSKET_PILLAGER.get(), MusketPillager.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
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

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
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