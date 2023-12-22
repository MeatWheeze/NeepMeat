package com.neep.neepmeat;

import com.neep.neepmeat.block.BlockInitialiser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class RealisticFluidClient implements ModInitializer
{
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(BlockInitialiser.STILL_TEST, BlockInitialiser.FLOWING_TEST, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xbb1d1d
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), BlockInitialiser.STILL_TEST, BlockInitialiser.FLOWING_TEST);

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
