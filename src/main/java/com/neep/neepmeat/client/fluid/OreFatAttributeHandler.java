package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class OreFatAttributeHandler implements FluidVariantAttributeHandler
{
    public Text getName(FluidVariant fluidVariant)
    {
        OreFatRegistry.Entry entry = OreFatRegistry.getFromVariant(fluidVariant);
        if (entry != null)
        {
            // TODO: use an enum or something
            if (fluidVariant.isOf(NMFluids.STILL_DIRTY_ORE_FAT))
                return entry.dirtyFatname();
            else
                return entry.cleanFatName();
        }
        return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName();
    }
}
