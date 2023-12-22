package com.neep.neepmeat.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public abstract class BFSGroupFinder<T>
{
    private final Queue<BlockPos> posQueue = new LinkedList<>();
    protected final Set<Long> visited = new ObjectOpenHashSet<>();
    private Long2ObjectArrayMap<T> result = new Long2ObjectArrayMap<>();

    public BFSGroupFinder()
    {
    }

    protected void addResult(BlockPos pos, T result)
    {
        this.result.put(pos.asLong(), result);
    }

    public Long2ObjectArrayMap<T> getResult()
    {
        return result;
    }

    public void reset()
    {
        posQueue.clear();
        visited.clear();
        result = new Long2ObjectArrayMap<>();
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
