package com.neep.neepmeat.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("UnstableApiUsage")
public interface MixableFluid
{
    static boolean canVariantsMix(FluidVariant variant1, FluidVariant variant2)
    {
        return variant1.getFluid() instanceof MixableFluid && variant2.getFluid() instanceof MixableFluid && variant1.isOf(variant2.getFluid());
    }

    void mixNbt(FluidVariant thisVariant, long thisAmount, FluidVariant otherVariant, long otherAmount);
}