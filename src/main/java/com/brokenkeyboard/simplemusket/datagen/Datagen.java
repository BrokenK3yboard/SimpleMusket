package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleMusket.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(generator));
            generator.addProvider(true, new GLMProvider(generator));
            generator.addProvider(true, new EntityTags(generator, event.getExistingFileHelper()));
        }
    }
}