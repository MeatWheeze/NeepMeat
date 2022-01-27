package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/*
    An interface for fluid networks associated with a FluidNodeProvider's face.
 */
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    public float pressure = 0;
    public FluidAcceptor.AcceptorModes mode;
    private FluidNetwork2 network = null;

    public FluidNode(BlockPos pos, Direction face)
    {
        this.face = face;
        this.pos = pos;
    }

    public void tick()
    {
        rebuildNetwork();
    }

    public void rebuildNetwork()
    {
        if (network == null)
        {
            network = new FluidNetwork2();
        }
        network.rebuild(pos);
    }

    public Direction getFace()
    {
        return face;
    }
}
