package com.neep.neepmeat.fluid_transfer;

import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class NetworkLookup
{
    protected Map<Long, PipeNetwork> networks;

    public PipeNetwork put(BlockPos pos, PipeNetwork network)
    {
        return networks.put(pos.asLong(), network);
    }

    public void remove(BlockPos pos)
    {
        networks.remove(pos.asLong());
    }

}
