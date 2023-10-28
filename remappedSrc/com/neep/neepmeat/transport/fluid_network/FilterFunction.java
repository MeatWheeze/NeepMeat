package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FilterFunction
{
    FilterFunction IDENTITY = (v, l) -> l;

    static long zero(FluidVariant variant, long l)
    {
        return 0;
    }

    static long identity(FluidVariant variant, long l)
    {
        return l;
    }

    long applyVariant(FluidVariant variant, long l);

    default FilterFunction andThen(@NotNull FilterFunction after)
    {
        // Avoid excessively chaining identity functions
        if (after == IDENTITY) return this;

        return (v, l) ->
        {
            return after.applyVariant(v, this.applyVariant(v, l));
        };
    }
}
