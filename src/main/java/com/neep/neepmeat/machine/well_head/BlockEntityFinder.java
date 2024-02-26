package com.neep.neepmeat.machine.well_head;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neep.neepmeat.NeepMeat;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BlockEntityFinder<T extends BlockEntity>
{
    private final World world;
    private final Set<ChunkPos> positions;
    private final Set<T> result = Sets.newHashSet();
    private final BlockEntityType<T> type;
    private Predicate<BlockEntity> predicate = b -> true;

    private final int updateInterval;
    private long lastUpdate = 0;

    public BlockEntityFinder(World world, BlockEntityType<T> type, int updateInterval)
    {
        this.world = world;
        this.type = type;
        this.updateInterval = updateInterval;
        this.positions = Sets.newHashSet();
    }

    public static Collection<ChunkPos> chunkRange(BlockPos pos)
    {
        ChunkPos origin = new ChunkPos(pos);

        List<ChunkPos> list = Lists.newArrayList();
        for (int i = -1; i <= 1; ++i)
        {
            for (int k = -1; k <= 1; ++k)
            {
                list.add(new ChunkPos(i + origin.x, k + origin.z));
            }
        }
        return list;
    }

    public BlockEntityFinder<T> addAll(Collection<ChunkPos> list)
    {
        positions.addAll(list);
        return this;
    }

    public BlockEntityFinder<T> predicate(Predicate<BlockEntity> predicate)
    {
        this.predicate = predicate;
        return this;
    }

    public void tick()
    {
        result.removeIf(BlockEntity::isRemoved);

        if (world.getTime() + updateInterval < lastUpdate)
        {
            return;
        }

        lastUpdate = world.getTime();

        result.clear();
        for (var chunkPos : positions)
        {
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            try
            {
                chunk.getBlockEntities().values().stream()
                        .filter(be -> be.getType().equals(type))
                        .filter(predicate)
                        .forEach(be -> result.add((T) be));
            }
            catch (ClassCastException e)
            {
                // Make this non-fatal because it's someone else's fault.
                NeepMeat.LOGGER.error("Block entity in chunk {} {}: class does not match type: {}", chunkPos.x, chunkPos.z, e.getMessage());
            }

        }

//        // If an update is not due but the iterator is not empty, continue.
//        if (world.getTime() + updateInterval < lastUpdate && !posIterator.hasNext())
//        {
//            return;
//        }
//        else if (!posIterator.hasNext()) // If an update is due and the iterator has finished
//        {
//            posIterator = positions.iterator();
//        }
//
//        // Checking one chunk per tick will hopefully reduce the performance impact, if any.
//        // My main worry is regularly checking a chunk that is made completely out of block entities.
//        if (posIterator.hasNext())
//        {
//            ChunkPos chunkPos = posIterator.next();
//
//            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
//
//            chunk.getBlockEntities().values().stream()
//                    .filter(be -> be.getType().equals(type))
//                    .forEach(be -> result.add((T) be));
//
//            result.removeIf(BlockEntity::isRemoved);
//        }
    }

    /**
     * Returns true when we can be sure that the world block entities have not changed.
     */
    public boolean notDirty()
    {
        return world.getTime() == lastUpdate;
    }

    public Set<T> result()
    {
        return result;
    }
}
