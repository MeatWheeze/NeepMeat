package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.renderer.GlassTankRenderer;
import com.neep.neepmeat.client.renderer.IntegratorEggRenderer;
import com.neep.neepmeat.client.renderer.ItemBufferRenderer;
import com.neep.neepmeat.client.renderer.TrommelRenderer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.init.FluidInitialiser;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class NeepMeatClient
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");

    public static void registerRenderers()
    {

        // Custom baked models
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());

        // BlockEntity renderers
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.ITEM_BUFFER_BLOCK_ENTITY, ItemBufferRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.TROMMEL_BLOCK_ENTITY, TrommelRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.INTEGRATOR, IntegratorEggRenderer::new);

        // Fluid textures
        FluidRenderHandlerRegistry.INSTANCE.register(FluidInitialiser.STILL_BLOOD, FluidInitialiser.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0x440d0e
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(FluidInitialiser.STILL_ENRICHED_BLOOD, FluidInitialiser.FLOWING_ENRICHED_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0xbb1d1d
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidInitialiser.STILL_BLOOD, FluidInitialiser.FLOWING_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidInitialiser.STILL_ENRICHED_BLOOD, FluidInitialiser.FLOWING_ENRICHED_BLOOD);

        //if you want to use custom textures they needs to be registered.
        //In this example this is unnecessary because the vanilla water textures are already registered.
        //To register your custom textures use this method.
        //ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
        //    registry.register(new Identifier("modid:block/custom_fluid_still"));
        //    registry.register(new Identifier("modid:block/custom_fluid_flowing"));
        //});

        // Block cutouts
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.RUSTED_BARS);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.SCAFFOLD_PLATFORM.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.SCAFFOLD_PLATFORM.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.BLUE_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.BLUE_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.BLUE_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.YELLOW_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.YELLOW_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.YELLOW_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.TANK_WALL);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInitialiser.SCAFFOLD_TRAPDOOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.FLUID_DRAIN);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SLOPE_TEST);

    }
}
