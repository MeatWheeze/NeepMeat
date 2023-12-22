package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.OreFatRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class OreFatAttributeHandler implements FluidVariantAttributeHandler
{
    public Text getName(FluidVariant fluidVariant)
    {
//        NbtCompound nbt = fluidVariant.getNbt();
        OreFatRegistry.Entry entry = OreFatRegistry.getFromVariant(fluidVariant);
        if (entry != null)
        {
            return (entry.name().copy()).append(" ").append(fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName());
        }
        return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName();
    }
}
