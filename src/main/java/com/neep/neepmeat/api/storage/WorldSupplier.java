package com.neep.neepmeat.api.storage;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.Supplier;

public interface WorldSupplier extends Supplier<ServerWorld>
{
    static WorldSupplier of(Supplier<World> supplier)
    {
        return () ->
        {
            if (supplier.get() instanceof ServerWorld serverWorld)
            {
                return serverWorld;
            }
            throw new IllegalArgumentException("WorldSupplier queried on the client!");
        };
    }

    static WorldSupplier of(BlockEntity supplier)
    {
        return () ->
        {
            if (supplier.getWorld() instanceof ServerWorld serverWorld)
            {
                return serverWorld;
            }
            throw new IllegalArgumentException("WorldSupplier queried on the client!");
        };
    }

    ServerWorld get();

    default Supplier<World> as()
    {
        return (Supplier<World>) (Object) this;
    }
}
