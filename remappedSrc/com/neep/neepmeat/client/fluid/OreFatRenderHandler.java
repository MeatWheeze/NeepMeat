package com.neep.neepmeat.client.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class OreFatRenderHandler extends SimpleFluidRenderHandler
{
    public OreFatRenderHandler(Identifier stillTexture, Identifier flowingTexture)
    {
        super(stillTexture, flowingTexture);
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas)
    {
        super.reloadTextures(textureAtlas);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state)
    {
        return tint;
    }
}
