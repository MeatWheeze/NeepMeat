package com.neep.neepmeat.fluid_util;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class PipeSegment extends Pair<BlockPos, Float>
{
    public PipeSegment(BlockPos pos, Float distance)
    {
        super(pos, distance);
    }
}
