package com.neep.neepmeat.transport.util;

import com.neep.neepmeat.transport.fluid_network.PipeState;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.DFSFinder;
import com.neep.neepmeat.util.IndexedHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class FluidPipeRouteFinder extends DFSFinder<PipeState.FilterFunction>
{
    protected PipeState.FilterFunction flowFunc;
    protected final Deque<PipeState.FilterFunction> filterStack = new ArrayDeque<>();
    protected final Deque<PipeState> pipeStack = new ArrayDeque<>();
    protected final Deque<Direction> directions = new ArrayDeque<>();
    protected PipeState currentPipe;
    protected final World world;
    protected final IndexedHashMap<BlockPos, PipeState> pipes;
    protected NodePos start;
    protected NodePos end;

    public FluidPipeRouteFinder(World world, IndexedHashMap<BlockPos, PipeState> pipes)
    {
        this.world = world;
        this.pipes = pipes;
        reset();
    }

//    static void thing()
//    {
//        BlockPos startPos = new BlockPos(0, 0, 0);
//        FluidPipeRouteFinder finder = new FluidPipeRouteFinder();
//        finder.pushBlock(startPos);
//        finder.loop(50);
//        if (finder.getResult() != null)
//        {
//            // Do things
//        }
//    }

    @Override
    public void reset()
    {
        super.reset();
        filterStack.clear();
        pipeStack.clear();
        directions.clear();
        flowFunc = PipeState::identity;
        currentPipe = null;
    }

    public void init(NodePos start, NodePos end)
    {
        reset();
        this.start = start;
        this.end = end;
        prepare(start.pos);
    }

    @Override
    public void prepare(BlockPos start)
    {
        super.prepare(start);
        currentPipe = pipes.get(start);
        directions.push(this.start.face);
        pipeStack.push(pipes.get(start));
        pipes.forEach(p -> p.flag = false);
        if (!processPipe(start, pipeStack.peek(), directions.peek(), world, filterStack))
        {
        }
    }

    @Override
    public BlockPos popBlock()
    {
        return super.popBlock();
    }

    private BlockPos offset;
    private PipeState offsetPipe;

    @Override
    protected State processPos(BlockPos current)
    {
        if (current.equals(end.pos))
        {
            for (PipeState.FilterFunction function : filterStack)
            {
                flowFunc = flowFunc.andThen(function);
            }
            setResult(end.pos, flowFunc);
            return State.SUCCESS;
        }

        currentPipe = pipeStack.peek();

        // Find an unvisited adjacent vertex and push it
        for (Direction connection : currentPipe.getConnections())
        {
            offset = current.offset(connection);
            offsetPipe = currentPipe.getAdjacent(connection);

            if (offsetPipe != null && !offsetPipe.flag)
            {

                // Flag next pipe as visited
                offsetPipe.flag = true;
                setVisited(offset);

                if (!processPipe(offset, offsetPipe, connection, world, filterStack)) continue;

                pushBlock(offset);
                pipeStack.push(offsetPipe);
                directions.push(connection);

                // Move to the new vertex, temporarily ignore all other connections
                return State.CONTINUE;
            }
        }

        popBlock();
        pipeStack.pop();
        directions.pop();
        filterStack.pop();

        return State.CONTINUE;
    }

    private static boolean processPipe(BlockPos pos, PipeState pipe, Direction direction, World world, Deque<PipeState.FilterFunction> filterStack)
    {
        // Check if pipe can transfer fluid in the opposite direction
        BlockState currentState = world.getBlockState(pos);
        if (!pipe.canFluidFlow(direction, currentState))
        {
            return false;
        }

        // Check if pipe has a special flow function.
        if (pipe.isSpecial())
        {
            filterStack.push(pipe.getSpecial().getFlowFunction(world, direction, pos, currentState));
        }
        else filterStack.push(PipeState::identity); // Default to identity function
        return true;
    }
}
