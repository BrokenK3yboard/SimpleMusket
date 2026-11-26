package com.brokenkeyboard.simplemusket.datagen;

import com.brokenkeyboard.simplemusket.Constants;
import com.brokenkeyboard.simplemusket.ModRegistry;
import com.brokenkeyboard.simplemusket.item.MusketItem;
import com.brokenkeyboard.simplemusket.item.MusketLoading;
import com.brokenkeyboard.simplemusket.item.MusketState;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.TransformVecBuilder;

import java.util.function.Consumer;

import static net.minecraft.client.data.models.ItemModelGenerators.createFlatModelDispatch;

public class SMModelGenerator extends ModelProvider {

    private static final Consumer<TransformVecBuilder> THIRD_PERSON_RIGHTHAND_ROTATION = transformVecBuilder -> transformVecBuilder.rotation(-80, 260, -40);
    private static final Consumer<TransformVecBuilder> THIRD_PERSON_LEFTHAND_ROTATION = transformVecBuilder -> transformVecBuilder.rotation(-80, -280, 40);
    private static final Consumer<TransformVecBuilder> THIRD_PERSON_TRANSLATION = transformVecBuilder -> transformVecBuilder.translation(-1, 0, 2.5F);

    private static final Consumer<TransformVecBuilder> FIRST_PERSON_RIGHTHAND_ROTATION = transformVecBuilder -> transformVecBuilder.rotation(0, -90, 45);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_LEFTHAND_ROTATION = transformVecBuilder -> transformVecBuilder.rotation(0, 90, -45);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_TRANSLATION = transformVecBuilder -> transformVecBuilder.translation(0, 3.2F, -0.96F);

    private static final Consumer<TransformVecBuilder> THIRD_PERSON_SCALE_DEFAULT = transformVecBuilder -> transformVecBuilder.scale(0.9F, 0.9F, 0.9F);
    private static final Consumer<TransformVecBuilder> THIRD_PERSON_SCALE_2X = transformVecBuilder -> transformVecBuilder.scale(1.8F, 1.8F, 0.9F);

    private static final Consumer<TransformVecBuilder> FIRST_PERSON_SCALE_DEFAULT = transformVecBuilder -> transformVecBuilder.scale(0.68F, 0.68F, 0.68F);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_SCALE_2X = transformVecBuilder -> transformVecBuilder.scale(1.36F, 1.36F, 0.68F);

    private static final Consumer<TransformVecBuilder> THIRD_PERSON_RIGHTHAND_ROTATION_LOADING = transformVecBuilder -> transformVecBuilder.rotation(-90, -87.75F, -135);
    private static final Consumer<TransformVecBuilder> THIRD_PERSON_LEFTHAND_ROTATION_LOADING = transformVecBuilder -> transformVecBuilder.rotation(-90, 87.75F, 135);
    private static final Consumer<TransformVecBuilder> THIRD_PERSON_TRANSLATION_LOADING = transformVecBuilder -> transformVecBuilder.translation(0, -7, 0);

    private static final Consumer<TransformVecBuilder> FIRST_PERSON_RIGHTHAND_ROTATION_LOADING = transformVecBuilder -> transformVecBuilder.rotation(0, -75, 0);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_LEFTHAND_ROTATION_LOADING = transformVecBuilder -> transformVecBuilder.rotation(0, -255, 0);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_TRANSLATION_LOADING = transformVecBuilder -> transformVecBuilder.translation(-4, 0.8F, 0);

    private static final Consumer<TransformVecBuilder> FIRST_PERSON_RIGHTHAND_ROTATION_AIMING = transformVecBuilder -> transformVecBuilder.rotation(0, -90, 25);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_LEFTHAND_ROTATION_AIMING = transformVecBuilder -> transformVecBuilder.rotation(0, 90, -25);
    private static final Consumer<TransformVecBuilder> FIRST_PERSON_TRANSLATION_AIMING = transformVecBuilder -> transformVecBuilder.translation(1.13F, 3.2F, 1.13F);

    private static final Consumer<TransformVecBuilder> THIRD_PERSON_TRANSLATION_SAWNOFF = transformVecBuilder -> transformVecBuilder.translation(-1, 0, -0.2F);

    public SMModelGenerator(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        generateMusket(ModRegistry.MUSKET.value(), itemModels);
        itemModels.generateFlatItem(ModRegistry.CARTRIDGE.value(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistry.GUNSLINGER_EGG.value(), ModelTemplates.FLAT_ITEM);
    }

    private void generateMusket(Item item, ItemModelGenerators itemModels) {
        ItemModel.Unbaked flatModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
        ModelTemplate baseTemplate = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket")).build();
        ModelTemplate loadedTemplate = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket")).requiredTextureSlot(TextureSlot.LAYER0).build();

        ModelTemplate baseDisplayTemplate = ExtendedModelTemplateBuilder.builder().parent(Identifier.withDefaultNamespace("item/generated"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> THIRD_PERSON_RIGHTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(t))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, t -> THIRD_PERSON_LEFTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(t))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> FIRST_PERSON_RIGHTHAND_ROTATION.andThen(FIRST_PERSON_TRANSLATION).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(t))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, t -> FIRST_PERSON_LEFTHAND_ROTATION.andThen(FIRST_PERSON_TRANSLATION).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(t))
                .build();

        ModelTemplate loadingTemplate1 = ExtendedModelTemplateBuilder.builder().parent(Identifier.withDefaultNamespace("item/generated"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> THIRD_PERSON_RIGHTHAND_ROTATION_LOADING.andThen(THIRD_PERSON_TRANSLATION_LOADING).andThen(THIRD_PERSON_SCALE_2X).accept(transformVecBuilder))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> THIRD_PERSON_LEFTHAND_ROTATION_LOADING.andThen(THIRD_PERSON_TRANSLATION_LOADING).andThen(THIRD_PERSON_SCALE_2X).accept(transformVecBuilder))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transformVecBuilder -> FIRST_PERSON_RIGHTHAND_ROTATION_LOADING.andThen(FIRST_PERSON_TRANSLATION_LOADING).andThen(FIRST_PERSON_SCALE_2X).accept(transformVecBuilder))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transformVecBuilder -> FIRST_PERSON_LEFTHAND_ROTATION_LOADING.andThen(FIRST_PERSON_TRANSLATION_LOADING).andThen(FIRST_PERSON_SCALE_2X).accept(transformVecBuilder))
                .build();

        ModelTemplate loadingTemplate2 = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket_loading_1"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .build();

        ModelTemplate loadingTemplate3 = ExtendedModelTemplateBuilder.builder().parent(Identifier.withDefaultNamespace("item/generated"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> THIRD_PERSON_RIGHTHAND_ROTATION_LOADING.andThen(THIRD_PERSON_TRANSLATION_LOADING).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> THIRD_PERSON_LEFTHAND_ROTATION_LOADING.andThen(THIRD_PERSON_TRANSLATION_LOADING).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transformVecBuilder -> FIRST_PERSON_RIGHTHAND_ROTATION_LOADING.andThen(FIRST_PERSON_TRANSLATION_LOADING).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transformVecBuilder -> FIRST_PERSON_LEFTHAND_ROTATION_LOADING.andThen(FIRST_PERSON_TRANSLATION_LOADING).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .build();

        ModelTemplate aimingTemplate = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transformVecBuilder -> FIRST_PERSON_RIGHTHAND_ROTATION_AIMING.andThen(FIRST_PERSON_TRANSLATION_AIMING).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transformVecBuilder -> FIRST_PERSON_LEFTHAND_ROTATION_AIMING.andThen(FIRST_PERSON_TRANSLATION_AIMING).andThen(FIRST_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .build();

        ModelTemplate sawnoffTemplate = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> THIRD_PERSON_RIGHTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION_SAWNOFF).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> THIRD_PERSON_LEFTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION_SAWNOFF).andThen(THIRD_PERSON_SCALE_DEFAULT).accept(transformVecBuilder))
                .build();

        ModelTemplate bayonetTemplate = ExtendedModelTemplateBuilder.builder().parent(ModRegistry.location("item/musket"))
                .requiredTextureSlot(TextureSlot.LAYER0)
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> THIRD_PERSON_RIGHTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION).andThen(THIRD_PERSON_SCALE_2X).accept(transformVecBuilder))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> THIRD_PERSON_LEFTHAND_ROTATION.andThen(THIRD_PERSON_TRANSLATION).andThen(THIRD_PERSON_SCALE_2X).accept(transformVecBuilder))
                .build();

        ItemModel.Unbaked base = ItemModelUtils.plainModel(baseDisplayTemplate.create(item, TextureMapping.layer0(item), itemModels.modelOutput));
        ItemModel.Unbaked loading1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_loading_1", loadingTemplate1));
        ItemModel.Unbaked loading2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_loading_2", loadingTemplate2));
        ItemModel.Unbaked loading3 = ItemModelUtils.plainModel(loadingTemplate3.create(ModelLocationUtils.getModelLocation(item, "_loading_3"), TextureMapping.layer0(item), itemModels.modelOutput));
        ItemModel.Unbaked loading4 = ItemModelUtils.plainModel(loadingTemplate2.create(ModelLocationUtils.getModelLocation(item, "_loading_4"), TextureMapping.layer0(TextureMapping.getItemTexture(item, "_loading_3")), itemModels.modelOutput));
        ItemModel.Unbaked loading5 = ItemModelUtils.plainModel(loadingTemplate2.create(ModelLocationUtils.getModelLocation(item, "_loading_5"), TextureMapping.layer0(TextureMapping.getItemTexture(item, "_loading_4")), itemModels.modelOutput));
        ItemModel.Unbaked loading6 = ItemModelUtils.plainModel(loadingTemplate2.create(ModelLocationUtils.getModelLocation(item, "_loading_6"), TextureMapping.layer0(TextureMapping.getItemTexture(item, "_loading_3")), itemModels.modelOutput));
        ItemModel.Unbaked loading7 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_loading_7", baseTemplate));
        ItemModel.Unbaked loaded = ItemModelUtils.plainModel(loadedTemplate.create(ModelLocationUtils.getModelLocation(item, "_loaded"), TextureMapping.layer0(TextureMapping.getItemTexture(item, "_loaded")), itemModels.modelOutput));
        ItemModel.Unbaked aiming = ItemModelUtils.plainModel(aimingTemplate.create(ModelLocationUtils.getModelLocation(item, "_aiming"), TextureMapping.layer0(TextureMapping.getItemTexture(item, "_loaded")), itemModels.modelOutput));
        ItemModel.Unbaked sawnoff = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_sawnoff", sawnoffTemplate));
        ItemModel.Unbaked bayonet = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_bayonet", bayonetTemplate));

        itemModels.itemModelOutput.accept(item, createFlatModelDispatch(flatModel, ItemModelUtils.select(new MusketState(), base,
                ItemModelUtils.when(MusketItem.UseState.NONE, base),
                ItemModelUtils.when(MusketItem.UseState.LOADED, loaded),
                ItemModelUtils.when(MusketItem.UseState.AIMING, aiming),
                ItemModelUtils.when(MusketItem.UseState.SAWNOFF, sawnoff),
                ItemModelUtils.when(MusketItem.UseState.BAYONET, bayonet),
                ItemModelUtils.when(MusketItem.UseState.LOADING, ItemModelUtils.rangeSelect(new MusketLoading(), loading1,
                        ItemModelUtils.override(loading2, 0.12F),
                        ItemModelUtils.override(loading3, 0.24F),
                        ItemModelUtils.override(loading4, 0.60F),
                        ItemModelUtils.override(loading5, 0.75F),
                        ItemModelUtils.override(loading6, 0.9F),
                        ItemModelUtils.override(loading7, 1))))
        ));
    }
}
