package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.brokenkeyboard.simplemusket.ModRegistry.BULLET_ENTITY;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {

    private static final RegistrySetBuilder DAMAGETYPE_BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, Datagen::bootstrap);
    private static final RegistrySetBuilder ENCHANTMENT_BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT, Datagen::enchantmentBootstrap);

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), DAMAGETYPE_BUILDER, Set.of(Constants.MOD_ID));
        // DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), ENCHANTMENT_BUILDER, Set.of(Constants.MOD_ID));
        CompletableFuture<HolderLookup.Provider> lookupProvider = provider.getRegistryProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(output));
            generator.addProvider(true, provider);
            generator.addProvider(true, new DamageTags(output, lookupProvider, event.getExistingFileHelper()));
            generator.addProvider(true, new EntityTags(output, lookupProvider, event.getExistingFileHelper()));
            generator.addProvider(true, new GLMProvider(output, lookupProvider));
        }
    }

    protected static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(Constants.BULLET, new DamageType("bullet", 0.5F));

    }

    protected static void enchantmentBootstrap(BootstrapContext<Enchantment> context) {
        context.register(ModRegistry.FIREPOWER,
                Enchantment.enchantment(Enchantment.definition(context.lookup(Registries.ITEM)
                        .getOrThrow(net.minecraft.tags.ItemTags.BOW_ENCHANTABLE), 10, 5, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(16, 10), 1, EquipmentSlotGroup.MAINHAND))
                        .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(0.5F)), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(BULLET_ENTITY)
                                .build())).build(ModRegistry.FIREPOWER.location()));
    }
}