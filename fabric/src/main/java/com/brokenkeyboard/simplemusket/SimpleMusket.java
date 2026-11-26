package com.brokenkeyboard.simplemusket;

import com.brokenkeyboard.simplemusket.item.BulletDispenseBehavior;
import com.brokenkeyboard.simplemusket.network.S2CSoundPayload;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.config.ModConfig;

public class SimpleMusket implements ModInitializer {

    public static final AttachmentType<Boolean> IS_USING_BAYONET = AttachmentRegistry.create(ModRegistry.location(Constants.USING_BAYONET), builder -> builder
            .syncWith(ByteBufCodecs.BOOL, (attachmentTarget, player) -> attachmentTarget instanceof Mob mob && PlayerLookup.tracking(mob).contains(player)));

    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Config.SPEC);
        ModRegistry.createEntityAttributes(FabricDefaultAttributeRegistry::register);
        SpawnPlacements.register(ModRegistry.GUNSLINGER.value(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        ModRegistry.registerSensorGoal();

        DispenserBlock.registerBehavior(ModRegistry.CARTRIDGE.value(), new BulletDispenseBehavior(ModRegistry.CARTRIDGE.value()));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.MUSKET.value()));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(content -> content.accept(ModRegistry.CARTRIDGE.value()));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> content.accept(ModRegistry.GUNSLINGER_EGG.value()));

        PayloadTypeRegistry.clientboundPlay().register(S2CSoundPayload.TYPE, S2CSoundPayload.STREAM_CODEC);

        FabricLoader.getInstance().getObjectShare().put(Constants.USEFUL_SPYGLASS_MOD_ID + ":" + ModRegistry.MUSKET, ModRegistry.AIMING_MUSKET);
    }
}