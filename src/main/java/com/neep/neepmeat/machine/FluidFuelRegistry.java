package com.neep.neepmeat.machine;

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
        put(fluid, new Entry(multiplier, isEnergised, exhaustType));
    }

    public static record Entry(float multiplier, boolean isEnergised, @Nullable Fluid exhaustType)
    {
        public boolean hasExhaust()
        {
            return exhaustType != null;
        }
    }
}
