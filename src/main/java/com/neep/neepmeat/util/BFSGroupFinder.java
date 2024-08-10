package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class BFSGroupFinder<T>
{
    private final Queue<BlockPos> posQueue = new LinkedList<>();
    protected final LongSet visited = new LongOpenHashSet();
    private final Map<BlockPos, T> result = new HashMap<>();

    public BFSGroupFinder()
    {
    }

    protected void addResult(BlockPos pos, T result)
    {
        this.result.put(pos.toImmutable(), result);
    }

    public Map<BlockPos, T> getResult()
    {
        return result;
    }

    public void reset()
    {
        posQueue.clear();
        visited.clear();
        result.clear();
    }

    public void queueBlock(BlockPos pos)
    {
        if (!visited.contains(pos.asLong()))
        {
            posQueue.add(pos.toImmutable());
            visited.add(pos.asLong());
        }
    }

    public void loop(int maxDepth)
    {
        int depth = 0;
        while (propagate(maxDepth) && depth < maxDepth)
        {
            ++depth;
        }
    }

    public boolean propagate(int maxDepth)
    {
        if (!posQueue.isEmpty())
        {
            BlockPos current = posQueue.poll();
            State state = processPos(current);
            return state == State.CONTINUE;
        }
        return false;
    }

    protected abstract State processPos(BlockPos pos);

    protected enum State
    {
        CONTINUE,
        SUCCESS,
        FAIL;
    }
}
