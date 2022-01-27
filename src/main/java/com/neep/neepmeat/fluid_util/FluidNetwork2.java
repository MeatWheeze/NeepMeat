package com.neep.neepmeat.fluid_util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidNetwork2
{
    private List<FluidNode> connectedNodes = new ArrayList<>();

    private Map<BlockPos, Float> connectedPipes = new HashMap<>();
    private List<BlockPos> pipeQueue = new ArrayList<>();

    public void rebuild(BlockPos startPos)
    {

    }

    public void discoverNodes(BlockPos startPos)
    {

    }

}
