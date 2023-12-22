package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.blockentity.TrommelBlockEntity;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.renderer.GlassTankRenderer;
import com.neep.neepmeat.client.renderer.ItemBufferRenderer;
import com.neep.neepmeat.client.renderer.TrommelRenderer;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class NeepMeatClient
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");

    public static void registerRenderers()
    {

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());

//        this.registerItemModel(ItemOs.REDSTONE);
//        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.REDSTONE_WIRE).with(When.anyOf(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE), When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP})), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R270)));

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.ITEM_BUFFER_BLOCK_ENTITY, ItemBufferRenderer::new);

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.TROMMEL_BLOCK_ENTITY, TrommelRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(BlockInitialiser.STILL_BLOOD, BlockInitialiser.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xbb1d1d
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), BlockInitialiser.STILL_BLOOD, BlockInitialiser.FLOWING_BLOOD);


        //if you want to use custom textures they needs to be registered.
        //In this example this is unnecessary because the vanilla water textures are already registered.
        //To register your custom textures use this method.
        //ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
        //    registry.register(new Identifier("modid:block/custom_fluid_still"));
        //    registry.register(new Identifier("modid:block/custom_fluid_flowing"));
        //});

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.RUSTED_BARS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInitialiser.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_STAIRS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInitialiser.SCAFFOLD_TRAPDOOR);

    }
}
