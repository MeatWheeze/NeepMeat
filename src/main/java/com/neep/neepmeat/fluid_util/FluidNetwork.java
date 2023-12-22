package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PipeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class FluidNetwork
{
    private final Direction face;
    private final BlockPos nodePos;
    private float pressure;

    // Positions and distances of connected pipes
    private Map<BlockPos, Float> visitedPipes = new HashMap<>();
    private List<BlockPos> nextPipes = new ArrayList<>();

    private List<FluidNetwork> connectedNodes = new ArrayList<>();

    /*
    Idea 1:
    - Each fluid BE has a network for each side, each network maintains its own list of connected nodes.

    Idea 2:
    - Networks are shared between connected nodes.
    - When the network is ticked, pressures are calculated
    - The network is ticked and rebuilt whenever a node is ticked.
     */

    public FluidNetwork(Direction face, BlockPos nodePos)
    {
        this.face = face;
        this.nodePos = nodePos;
    }

    public void tick()
    {
        for (FluidNetwork node : connectedNodes)
        {
        }
    }

    /*
        Volumetric flow for laminar Poiseuille flow:
        Q = - (πR^2) / (8µ) * dp/dx
        R and µ chosen arbitrarily
     */
    public void transmitFluid(FluidNetwork node, int quantity)
    {
    }

    public void receiveFluid()
    {

    }

    // This abomination builds a list of connected pipes and nodes.
    public void refresh(World world)
    {
        BlockPos start = nodePos.offset(face);
        BlockState state = world.getBlockState(start);
        List<BlockPos> nodes = new ArrayList<>();
        if ((state.getBlock() instanceof FluidNodeProvider))
        {
            return;
        }
        if (state.getBlock() instanceof PipeBlock)
        {

            visitedPipes.clear();
            nextPipes.clear();
            nodes.clear();

            // List of pipes to be searched in next iteration
            List<BlockPos> connected = new ArrayList<>();

            visitedPipes.put(nodePos, 10f);
//            visitedPipes.put(start, 10f);
            nextPipes.add(nodePos);

            // Pipe search depth
            for (int i = 0; i < 10; ++i)
            {
                connected.clear();
                for (ListIterator<BlockPos> iterator = nextPipes.listIterator(); iterator.hasNext();)
                {
                    BlockPos current = iterator.next();

                    for (Direction direction : Direction.values())
                    {
                        BlockPos next = current.offset(direction);
                        BlockState state1 = world.getBlockState(current);
                        BlockState state2 = world.getBlockState(next);

                        if (FluidAcceptor.isConnectedIn(state1, direction) && !visitedPipes.containsKey(next))
                        {
                            // Check that target is a pipe and not a fluid block entity
                            if (state2.getBlock() instanceof FluidAcceptor
                                    && !(state2.getBlock() instanceof FluidNodeProvider))
                            {
                                // Next block is connected in opposite direction
                                if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                                {
                                    connected.add(next);
                                    // Estimated frictional head loss

                                    visitedPipes.put(next, (float) i);
                                }
                            }
                            else if (state2.getBlock() instanceof FluidNodeProvider)
                            {
                                if (((FluidNodeProvider) state2.getBlock()).connectInDirection(state2, direction.getOpposite()))
                                {
                                    System.out.println("target: " + next.toString());
                                    nodes.add(next);
                                }
                            }
                        }
                    }
                    iterator.remove();
                }
                nextPipes.addAll(connected);
            }
            System.out.println(nodes);
        }
    }

    public void appendPipe()
    {

    }

    public void buildPressure()
    {

    }

    public Direction getFace()
    {
        return face;
    }

    public float getPressure()
    {
        return pressure;
    }

    public void setPressure(float pressure)
    {
        this.pressure = pressure;
    }
}
