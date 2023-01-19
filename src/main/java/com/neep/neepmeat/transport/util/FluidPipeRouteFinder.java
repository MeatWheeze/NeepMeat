package com.neep.neepmeat.transport.util;

import com.neep.neepmeat.transport.fluid_network.PipeState;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.DFSFinder;
import com.neep.neepmeat.util.IndexedHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.nio.channels.Pipe;
import java.util.ArrayDeque;
import java.util.Deque;

public class FluidPipeRouteFinder extends DFSFinder<PipeState.FilterFunction>
{
    protected PipeState.FilterFunction flowFunc;
    protected final Deque<PipeState.FilterFunction> filterStack = new ArrayDeque<>();
    protected final Deque<PipeState> pipeStack = new ArrayDeque<>();
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
        flowFunc = PipeState::identity;
        currentPipe = null;
    }

    public void init(NodePos start, NodePos end)
    {
        reset();
        this.start = start;
        this.end = end;
        prepare(start.pos, start.face.getOpposite());
    }

    @Override
    public void prepare(BlockPos start, Direction startDir)
    {
        super.prepare(start, startDir);
        currentPipe = pipes.get(start);
//        directions.push(this.start.face);
        pipeStack.push(pipes.get(start));
        pipes.forEach(p -> p.flag = false);
        filterStack.push(PipeState.IDENTITY);
//        if (!processPipe(start, pipeStack.peek(), directions.peek(), world, filterStack))
//        {
//            // This should cause an early return if the starting pipe is blocked.
//            // I hate myself for working around my own system
//            for (PipeState.FilterFunction function : filterStack)
//            {
//                flowFunc = flowFunc.andThen(function);
//            }
//            pushBlock(end.pos);
//            setResult(start, flowFunc);
//        }
    }

    private BlockPos.Mutable offset = new BlockPos.Mutable();
    private PipeState offsetPipe;

    @Override
    protected State processPos(BlockPos current, Direction fromDir)
    {
        currentPipe = pipeStack.peek();

        if (!canFlow(current, currentPipe, fromDir, world, filterStack))
        {
            return State.FAIL;
        }

        if (current.equals(end.pos))
        {
            for (PipeState.FilterFunction function : filterStack)
            {
                flowFunc = flowFunc.andThen(function);
            }
            setResult(end.pos, flowFunc);
            return State.SUCCESS;
        }

        // Find the first non-visited adjacent vertex and push it.
        for (Direction connection : currentPipe.getConnections())
        {
            offset.set(current, connection);
            offsetPipe = currentPipe.getAdjacent(connection);

            if (offsetPipe != null && !offsetPipe.flag)
            {
                // Flag next pipe as visited
                offsetPipe.flag = true;
                setVisited(offset);

                // Push next vertex to the stack
                pushBlock(offset, connection);
                pipeStack.push(offsetPipe);

                if (offsetPipe.isSpecial())
                {
                    BlockState currentState = world.getBlockState(offset);
                    filterStack.push(offsetPipe.getSpecial().getFlowFunction(world, connection, offset, currentState));
                }
                else filterStack.push(PipeState.IDENTITY); // Default to identity function

                // Temporarily ignore all other connections
                return State.CONTINUE;
            }
        }

        // Remove the current vertex from the stack.
        popBlock();
        popDir();
        pipeStack.pop();
        filterStack.pop();

        return State.CONTINUE;
    }

    private static boolean canFlow(BlockPos pos, PipeState pipe, Direction direction, World world, Deque<PipeState.FilterFunction> filterStack)
    {
        // Check if pipe can transfer fluid in the opposite direction
        BlockState currentState = world.getBlockState(pos);
        return pipe.canFluidFlow(direction, currentState);

        // Check if pipe has a special flow function.
//        if (pipe.isSpecial())
//        {
//            filterStack.push(pipe.getSpecial().getFlowFunction(world, direction, pos, currentState));
//        }
//        else filterStack.push(PipeState.IDENTITY); // Default to identity function
    }
}
