package com.neep.neepmeat.client;

import com.neep.meatlib.block.BasePaintedBlock;
import com.neep.meatweapons.client.model.PlasmaEntityModel;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.model.SwordModel;
import com.neep.neepmeat.client.renderer.*;
import com.neep.neepmeat.client.screen.BufferScreen;
import com.neep.neepmeat.client.screen.ContentDetectorScreen;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.network.TankMessagePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.ArrayList;
import java.util.List;

public class NeepMeatClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");
    public static final EntityModelLayer TANK_MINECART = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "tank_minecart"), "main");

    public static List<BasePaintedBlock.PaintedBlock> COLOURED_BLOCKS = new ArrayList<>();

    public static final Identifier CHARGED_WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_flowing");
    public static final Identifier CHARGED_WORK_FLUID= new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_still");
    public static final Identifier WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_flowing");
    public static final Identifier WORK_FLUID = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_still");

    @Override
    public void onInitializeClient()
    {
        NeepMeatClient.registerRenderers();

        TankMessagePacket.registerReciever();
    }

    public static void registerRenderers()
    {
        // Custom baked models
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(NMExtraModels.EXTRA_MODELS);

        // BlockEntity renderers
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.FLUID_BUFFER, FluidBufferRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, ItemBufferRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.TROMMEL_BLOCK_ENTITY, TrommelRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.INTEGRATOR, IntegratorEggRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.BIG_LEVER, BigLeverRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.PNEUMATIC_PIPE, PneumaticPipeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.ITEM_PUMP, ItemPumpRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.EJECTOR, EjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(NMBlockEntities.CONVERTER, ConverterRenderer::new);

        EntityRendererRegistry.register(NMEntities.TANK_MINECART, (ctx) -> new TankMinecartRenderer(ctx, TANK_MINECART));
        EntityModelLayerRegistry.registerModelLayer(TANK_MINECART, MinecartEntityModel::getTexturedModelData);

        GeoItemRenderer.registerItemRenderer(NMItems.SLASHER, new SwordRenderer<>(new SwordModel<>(
                NeepMeat.NAMESPACE,
                "geo/slasher.geo.json",
                "textures/item/slasher.png",
                "animations/slasher.animation.json"
        )));

        GeoItemRenderer.registerItemRenderer(NMItems.CHEESE_CLEAVER, new SwordRenderer<>(new SwordModel<>(
                NeepMeat.NAMESPACE,
                "geo/cheese_cleaver.geo.json",
                "textures/item/cheese_cleaver.png",
                "animations/cheese_cleaver.animation.json"
        )));

        // Fluid textures
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0x440d0e
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_ENRICHED_BLOOD, NMFluids.FLOWING_ENRICHED_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0xbb1d1d
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_WORK_FLUID, NMFluids.FLOWING_WORK_FLUID, new SimpleFluidRenderHandler(
                CHARGED_WORK_FLUID,
                CHARGED_WORK_FLUID,
                0x999999
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_CHARGED_WORK_FLUID, NMFluids.FLOWING_CHARGED_WORK_FLUID, new SimpleFluidRenderHandler(
                CHARGED_WORK_FLUID,
                CHARGED_WORK_FLUID,
                0xFFFFFF
        ));

        //if you want to use custom textures they needs to be registered.
        //In this example this is unnecessary because the vanilla water textures are already registered.
        //To register your custom textures use this method.
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(CHARGED_WORK_FLUID);
            registry.register(CHARGED_WORK_FLUID_FLOWING);
        });

        // Coloured blocks
        for (BasePaintedBlock.PaintedBlock block : COLOURED_BLOCKS)
        {
            ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> block.getColour(), block);
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> block.getColour(), block.asItem());
        }

        // Fluids
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_ENRICHED_BLOOD, NMFluids.FLOWING_ENRICHED_BLOOD);


        // Block cutouts
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_BUFFER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.RUSTED_BARS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.PNEUMATIC_TUBE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.ITEM_PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.CONVERTER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.LEADED_GLASS);
//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.LARGE_CONVERTER);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.SCAFFOLD_PLATFORM.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.SCAFFOLD_PLATFORM.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BLUE_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.BLUE_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.BLUE_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.YELLOW_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.YELLOW_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.YELLOW_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.TANK_WALL);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), NMBlocks.SCAFFOLD_TRAPDOOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_DRAIN);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SLOPE_TEST);

        // Screens
        ScreenRegistry.register(ScreenHandlerInit.BUFFER_SCREEN_HANDLER, BufferScreen::new);
        ScreenRegistry.register(ScreenHandlerInit.CONTENT_DETECTOR_SCREEN_HANDLER, ContentDetectorScreen::new);

    }
}
