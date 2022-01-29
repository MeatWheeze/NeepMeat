package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
    private FluidNetwork network = null;
    public Map<FluidNode, Integer> distances = new HashMap<>();
    private final Storage<FluidVariant> storage;

    public FluidNode(BlockPos pos, Direction face, Storage<FluidVariant> storage, FluidAcceptor.AcceptorModes mode, float pressure)
    {
        this.face = face;
        this.pos = pos;
        this.storage = storage;
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        return "\n" + this.pos.toString() + " " + face;
    }

    public void setNetwork(FluidNetwork network)
    {
        this.network = network;
        distances.clear();
    }

    public void tick(World world)
    {
        if (network != null)
        {
            network.tick();
        }
    }

    public void rebuildNetwork(World world)
    {
        if (network == null)
        {
            network = new FluidNetwork(world);
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
        if (distances.get(node) == null)
        {
            System.out.println("transmit null");
            return;
        }
        float pressureGradient = (node.getPressure() - getPressure()) / distances.get(node);
//        System.out.println(node + ", " + distances.get(node) + ", " + pressureGradient);
    }
}
