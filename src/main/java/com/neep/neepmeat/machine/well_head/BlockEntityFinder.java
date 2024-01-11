package com.neep.neepmeat.machine.well_head;

import com.google.common.collect.Sets;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockEntityFinder<T extends BlockEntity>
{
    private final World world;
    private final Set<ChunkPos> positions;
    private final Set<T> result = Sets.newHashSet();
    private final BlockEntityType<T> type;

    private final int updateInterval;
    private long lastUpdate = 0;

    private Iterator<ChunkPos> posIterator = Collections.emptyIterator();

    public BlockEntityFinder(World world, BlockEntityType<T> type, int updateInterval)
    {
        this.world = world;
        this.type = type;
        this.updateInterval = updateInterval;
        this.positions = Sets.newHashSet();
    }

    public BlockEntityFinder<T> add(ChunkPos... chunkPos)
    {
        positions.addAll(List.of(chunkPos));
        posIterator = positions.iterator();
        return this;
    }

    public void tick()
    {
        // If an update is not due but the iterator is not empty, continue.
        if (world.getTime() + updateInterval < lastUpdate && !posIterator.hasNext())
        {
            return;
        }
        else if (!posIterator.hasNext()) // If an update is due and the iterator has finished
        {
            posIterator = positions.iterator();
        }

        // Checking one chunk per tick will hopefully reduce the performance impact, if any.
        // My main worry is regularly checking a chunk that is made completely out of block entities.
        if (posIterator.hasNext())
        {
            ChunkPos chunkPos = posIterator.next();

            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            chunk.getBlockEntities().values().stream()
                    .filter(be -> be.getType().equals(type))
                    .forEach(be -> result.add((T) be));

            result.removeIf(BlockEntity::isRemoved);
        }
    }

    public Set<T> result()
    {
        return result;
    }
}
