package com.neep.neepmeat.api.processing;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FluidEnegyRegistry extends HashMap<Fluid, FluidEnegyRegistry.Entry>
{
    protected static final FluidEnegyRegistry INSTANCE = new FluidEnegyRegistry();
    public static final com.neep.neepmeat.api.processing.FluidEnegyRegistry.Entry EMPTY = new com.neep.neepmeat.api.processing.FluidEnegyRegistry.Entry(0, false, null);

    public static FluidEnegyRegistry getInstance()
    {
        return INSTANCE;
    }

    /**
     * @param baseEnergy The energy in a droplet of this fluid
     */
    public void register(Fluid fluid, double baseEnergy, boolean isEnergised, @Nullable Fluid exhaustType)
    {
        put(fluid, new FluidEnegyRegistry.Entry(baseEnergy, isEnergised, exhaustType));
    }

    @NotNull
    public com.neep.neepmeat.api.processing.FluidEnegyRegistry.Entry getOrEmpty(Fluid fluid)
    {
        return getOrDefault(fluid, EMPTY);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static record Entry(double baseEnergy, boolean isEnergised, @Nullable Fluid exhaustType)
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
