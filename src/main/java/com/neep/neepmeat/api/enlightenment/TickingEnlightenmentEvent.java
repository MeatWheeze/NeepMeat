package com.neep.neepmeat.api.enlightenment;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class TickingEnlightenmentEvent implements EnlightenmentEvent
{
    protected int time;
    protected boolean removed;

    protected final ServerWorld world;
    protected final ServerPlayerEntity player;

    protected TickingEnlightenmentEvent(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
    {
        this.world = world;
        this.player = player;
    }

    @Override
    public void spawn()
    {
        EnlightenmentEvent.super.spawn();
    }

    @Override
    public void tick()
    {
        ++time;
    }

    public void markRemoved()
    {
        this.removed = true;
    }

    @Override
    public boolean isRemoved()
    {
        return removed;
    }

//    public static class Factory implements EnlightenmentEvent.Factory
//    {
//        @Override
//        public EnlightenmentEvent create(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
//        {
//            return new TickingEnlightenmentEvent(manager, world, player);
//        }
//
//        @Override
//        public boolean willSpawn(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
//        {
//            return true;
//        }
//    }
}
