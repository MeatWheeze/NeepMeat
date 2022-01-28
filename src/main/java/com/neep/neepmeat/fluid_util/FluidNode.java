package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/*
    An interface for fluid networks associated with a FluidNodeProvider's face.
 */
public class FluidNode
{
    private final Direction face;
    private final BlockPos pos;
    public float pressure = 4;
    public FluidAcceptor.AcceptorModes mode;
    private FluidNetwork2 network = null;
    public Map<FluidNode, Integer> distances = new HashMap<>();

    public FluidNode(BlockPos pos, Direction face, FluidAcceptor.AcceptorModes mode)
    {
        this.face = face;
        this.pos = pos;
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face;
    }

    public void setNetwork(FluidNetwork2 network)
    {
        this.network = network;
        distances.clear();
    }

    public void tick(World world)
    {
//        rebuildNetwork(world);
        if (network != null)
        {
            network.tick();
        }
    }

    public void rebuildNetwork(World world)
    {
        if (network == null)
        {
            network = new FluidNetwork2(world);
        }
        network.rebuild(pos, face);
    }

    public Direction getFace()
    {
        return face;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public float getPressure()
    {
        return pressure;
    }

    public void transmitFluid(FluidNode node)
    {
        float pressureGradient = (node.getPressure() - getPressure()) / distances.get(node);
//        System.out.println(node + ", " + distances.get(node) + ", " + pressureGradient);
    }
}
