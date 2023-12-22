package com.neep.neepmeat.fluid_util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.level.storage.AnvilLevelStorage;

import java.util.HashMap;
import java.util.Map;

public class FluidNetwork extends AnvilLevelStorage
{
    public static final FluidNetwork NETWORK = new FluidNetwork();

    public Map<ChunkPos, PipeBranches> chunkPipes= new HashMap<>();


    public PipeBranches getOrCreateMap(ChunkPos pos)
    {
        PipeBranches out;
        if ((out = chunkPipes.get(pos)) != null)
        {
            return out;
        }
        chunkPipes.put(pos, out = new PipeBranches());
        return out;
    }

    public void updateSegment(BlockPos pos, PipeSegment segment)
    {
        PipeBranches branch = getOrCreateMap(ChunkSectionPos.from(pos).toChunkPos());
        branch.put(ChunkSectionPos.toLong(pos), segment);
    }

    public void removeSegment(BlockPos pos)
    {
        PipeBranches branch;
        if ((branch = chunkPipes.get(ChunkSectionPos.from(pos).toChunkPos())) == null)
        {
            return;
        }
        branch.remove(ChunkSectionPos.toLong(pos));
    }
}
