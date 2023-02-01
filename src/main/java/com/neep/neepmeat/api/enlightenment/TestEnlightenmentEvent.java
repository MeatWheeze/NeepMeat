package com.neep.neepmeat.api.enlightenment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TestEnlightenmentEvent implements EnlightenmentEvent
{
    protected int time;
    protected boolean removed;

    protected final ServerWorld world;
    protected final ServerPlayerEntity player;

    protected TestEnlightenmentEvent(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
    {
        this.world = world;
        this.player = player;
    }

    @Override
    public void spawn()
    {
        EnlightenmentEvent.super.spawn();
        Entity entity = new PigEntity(EntityType.PIG, world);
        entity.setPos(player.getX(), player.getY(), player.getZ());
        world.spawnEntity(entity);
        markRemoved();
    }

    @Override
    public void tick()
    {
        ++time;
        if (time > 1) markRemoved();
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

    public static class Factory implements EnlightenmentEvent.Factory
    {
        @Override
        public EnlightenmentEvent create(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
        {
            return new TestEnlightenmentEvent(manager, world, player);
        }

        @Override
        public boolean willSpawn(EnlightenmentEventManager manager, ServerWorld world, ServerPlayerEntity player)
        {
            return true;
        }
    }
}
