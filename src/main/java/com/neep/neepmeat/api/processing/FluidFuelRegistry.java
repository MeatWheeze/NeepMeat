package com.neep.neepmeat.api.processing;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FluidFuelRegistry extends HashMap<Fluid, FluidFuelRegistry.Entry>
{
    protected static FluidFuelRegistry INSTANCE = new FluidFuelRegistry();

    public static FluidFuelRegistry getInstance()
    {
        return INSTANCE;
    }

    public void register(Fluid fluid, float multiplier, boolean isEnergised, @Nullable Fluid exhaustType)
    {
        put(fluid, new FluidFuelRegistry.Entry(multiplier, isEnergised, exhaustType));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static record Entry(float multiplier, boolean isEnergised, @Nullable Fluid exhaustType)
    {
        public boolean hasExhaust()
        {
            return exhaustType != null;
        }

        public @Nullable FluidVariant getExhaustVariant()
        {
            return hasExhaust() ? FluidVariant.of(exhaustType) : null;
        }
    }
}
