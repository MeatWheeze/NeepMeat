package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.util.IterateRandomly;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.*;

public class ReactionCoreBlockEntity extends SyncableBlockEntity
{

    public ReactionCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        if (world.getTime() % 40 == 0)
        {
            placeExudate();
        }
    }

    private void placeExudate()
    {
        List<BlockPos> potential = traverse(world, pos, 400, 5);

        int canPlace = Math.min(potential.size(), 5);
        for (int i = 0; i < canPlace; ++i)
        {
            BlockPos pos = potential.get(i);

            world.setBlockState(pos, NMBlocks.ACTIVE_WASTE.getDefaultState());
        }
    }

    private List<BlockPos> traverse(World world, BlockPos origin, int maxVisit, int maxToPlace)
    {
        Direction[] horDirections = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        LongSet visited = new LongOpenHashSet();
        Queue<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> positions = new ArrayList<>();

        if (!isValidBlock(world.getBlockState(origin)))
            return List.of();

        visited.add(origin.asLong());
        queue.add(origin);

        int blocksVisited = 0;
        while (!queue.isEmpty()
                && blocksVisited < maxVisit
                && positions.size() < maxToPlace
        )
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();
            for (int i : new IterateRandomly(horDirections.length))
            {
                Direction direction = horDirections[i];

                mutable.set(current, direction);
                if (!visited.contains(mutable.asLong()))
                {
                    visited.add(mutable.asLong());

                    BlockState offsetState = world.getBlockState(mutable);
                    if (offsetState.isAir())
                    {
                        positions.add(mutable.toImmutable());
                    }
                    else if (isValidBlock(offsetState))
                    {
                        blocksVisited++;
                        queue.add(mutable.toImmutable());
                    }
                }
            }
        }
        return positions;
    }

    private boolean isValidBlock(BlockState blockState)
    {
        return blockState.isOf(NMBlocks.ACTIVE_WASTE) || blockState.isOf(NMBlocks.REACTION_CORE);
    }
}
