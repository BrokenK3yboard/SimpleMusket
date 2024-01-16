package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.SimpleMusket;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import static com.brokenkeyboard.simplemusket.SimpleMusket.MUSKET_PILLAGER;

public class EntityTags extends EntityTypeTagsProvider {

    public EntityTags(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
        super(generator, SimpleMusket.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.RAIDERS).add(MUSKET_PILLAGER.get());
    }
}
