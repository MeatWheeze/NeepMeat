package com.neep.neepmeat.transport.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.chunk.WorldChunk;

@FunctionalInterface
public interface WorldChunkEvents
{
    Event<WorldChunkEvents> LOAD_ENTITIES = EventFactory.createArrayBacked(WorldChunkEvents.class, (listeners) -> ((chunk) ->
    {
        for (var listener : listeners)
        {
            listener.load(chunk);
        }
    }));

    Event<WorldChunkEvents> UNLOAD_ENTITIES = EventFactory.createArrayBacked(WorldChunkEvents.class, listeners -> (chunk) ->
    {
        for (var listener : listeners)
        {
            listener.load(chunk);
        }
    });

    Event<BlockEntityEvent> BE_SET_WORLD = EventFactory.createArrayBacked(BlockEntityEvent.class, listeners -> (chunk, be) ->
    {
        for (var listener : listeners)
        {
            listener.apply(chunk, be);
        }
    });

    Event<BlockEntityEvent> BE_MANUAL_REMOVE = EventFactory.createArrayBacked(BlockEntityEvent.class, listeners -> (chunk, be) ->
    {
        for (var listener : listeners)
        {
            listener.apply(chunk, be);
        }
    });

    void load(WorldChunk chunk);

    @FunctionalInterface
    interface BlockEntityEvent
    {
        void apply(WorldChunk chunk, BlockEntity be);
    }
}
