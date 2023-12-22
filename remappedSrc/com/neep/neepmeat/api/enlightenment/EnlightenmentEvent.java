package com.neep.neepmeat.api.enlightenment;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface EnlightenmentEvent
{
    default void spawn() {}
    void tick();

    boolean isRemoved();

    interface Factory
    {
        EnlightenmentEvent create(ServerWorld world, ServerPlayerEntity player);

        boolean willSpawn(ServerWorld world, ServerPlayerEntity player);
    }
}
