package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public abstract class DFSFinder<T>
{
    private final Deque<BlockPos> posStack = new ArrayDeque<>();
    private final Set<Long> visited = new LongOpenHashSet();
    private Pair<BlockPos, T> result;
    private State state;

    public DFSFinder()
    {
    }

    protected void setResult(BlockPos pos, T result)
    {
        this.result = Pair.of(pos, result);
    }

    public Pair<BlockPos, T> getResult()
    {
        return result;
    }

    protected boolean visited(BlockPos pos)
    {
        return visited.contains(pos.asLong());
    }

    protected void setVisited(BlockPos pos)
    {
        visited.add(pos.asLong());
    }

    public void reset()
    {
        posStack.clear();
        visited.clear();
        result = null;
    }

    public void pushBlock(BlockPos pos)
    {
        posStack.push(pos);
    }

    public BlockPos popBlock()
    {
        return posStack.pop();
    }

    public void prepare(BlockPos start)
    {
        pushBlock(start);
    }

    public void loop(int maxDepth)
    {
        while (propagate(maxDepth));
    }

    public boolean propagate(int maxDepth)
    {
        if (!posStack.isEmpty())
        {
            // This method should push one adjacent vertex
            state = processPos(posStack.peek());
            return state == State.CONTINUE;
        }
        return false;
    }

//    public State getState()
//    {
//        return state;
//    }
//
//    protected void setState(State state)
//    {
//        this.state = state;
//    }

    protected abstract State processPos(BlockPos current);

    public boolean hasResult()
    {
        return result != null;
    }

    public enum State
    {
        CONTINUE,
        REVERSE,
        FAIL,
        SUCCESS;
    }
}
