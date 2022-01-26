package com.neep.neepmeat;

import com.neep.neepmeat.init.BlockInitialiser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class RealisticFluidClient implements ModInitializer
{
    public void onInitializeClient() {

//        this.registerItemModel(Items.REDSTONE);
//        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.REDSTONE_WIRE).with(When.anyOf(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE), When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP})), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R270)));

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

        // ...
    }

    @Override
    public void onInitialize()
    {
        onInitializeClient();
    }
}
