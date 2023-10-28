package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.MeatFluidHelper;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class CoarseMeatVariantRenderHandler implements FluidVariantRenderHandler
{
    @Override
    public void appendTooltip(FluidVariant fluidVariant, List<Text> tooltip, TooltipContext tooltipContext)
    {
    }

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos)
    {
        return MeatFluidHelper.getColour(fluidVariant);
    }

    @Override
    @Nullable
    public Sprite[] getSprites(FluidVariant fluidVariant)
    {
        // Use the fluid render handler by default.
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidVariant.getFluid());

        if (fluidRenderHandler != null) {
            return fluidRenderHandler.getFluidSprites(null, null, fluidVariant.getFluid().getDefaultState());
        } else {
            return null;
        }
    }
}
