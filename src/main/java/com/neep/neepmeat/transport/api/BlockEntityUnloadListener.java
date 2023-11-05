package com.neep.neepmeat.transport.api;

import net.minecraft.world.chunk.WorldChunk;

public interface BlockEntityUnloadListener
{
    void onUnload(WorldChunk chunk);

//    void onRemove(WorldChunk chunk);
}
