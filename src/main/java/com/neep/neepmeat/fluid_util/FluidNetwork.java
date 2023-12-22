package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.level.storage.AnvilLevelStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FluidNetwork
{
    public static final FluidNetwork NETWORK = new FluidNetwork();

    public Map<ChunkPos, PipeBranches> chunkPipes= new HashMap<>();
    public Map<ChunkPos, Map<NodePos, Supplier<FluidNode>>> chunkNodes = new HashMap<>();


    public Map<NodePos, Supplier<FluidNode>> getOrCreateMap(ChunkPos pos)
    {
        Map<NodePos, Supplier<FluidNode>> out;
        if ((out = chunkNodes.get(pos)) != null)
        {
            return out;
        }
        chunkNodes.put(pos, out = new HashMap<>());
        return out;
    }

    public void updateSegment(NodePos pos, FluidNode node)
    {
        Map<NodePos, Supplier<FluidNode>> nodes = getOrCreateMap(pos.toChunkPos());
        nodes.put(pos, () -> node);
//        System.out.println("Node updated: " + nodes.get(pos).get());
    }

    public void removeSegment(NodePos pos)
    {
        Map<NodePos, Supplier<FluidNode>> nodes;
        if ((nodes = chunkNodes.get(pos.toChunkPos())) == null)
        {
//            System.out.println(pos);
            return;
        }
        if (nodes.get(pos) != null)
        {
//            System.out.println("Node removed: " + nodes.get(pos).get());
            nodes.remove(pos);
        }
    }
}
