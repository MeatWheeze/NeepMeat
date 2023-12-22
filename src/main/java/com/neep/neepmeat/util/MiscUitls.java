package com.neep.neepmeat.util;

import com.neep.neepmeat.api.block.pipe.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class MiscUitls
{
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    public static List<RetrievalTarget<ItemVariant>> floodSearch(BlockPos startPos, Direction face, World world, Predicate<Pair<BlockPos, Direction>> predicate, int depth)
    {
        List<BlockPos> pipeQueue = new ArrayList<>();
        List<BlockPos> nextSet = new ArrayList<>();
        List<BlockPos> visited = new ArrayList<>();
        List<RetrievalTarget<ItemVariant>> output = new ArrayList<>();

        pipeQueue.add(startPos.offset(face));
        visited.add(startPos.offset(face));

        for (int i = 0; i < depth; ++i)
        {
            nextSet.clear();
            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            {
                BlockPos current = iterator.next();

                for (Direction direction : Direction.values())
                {
                    BlockPos next = current.offset(direction);
                    BlockState currentState = world.getBlockState(current);
                    BlockState nextState = world.getBlockState(next);

                    if (IItemPipe.isConnectedIn(world, current, currentState, direction) && !visited.contains(next))
                    {
                        visited.add(next);

                        // Check that target is a pipe and not a fluid block entity
                        if (nextState.getBlock() instanceof IItemPipe && !(nextState.getBlock() instanceof ItemPumpBlock))
                        {
                            // Next block is connected in opposite direction
                            if (IItemPipe.isConnectedIn(world, next, nextState, direction.getOpposite()))
                            {
                                nextSet.add(next);
                            }
                        }
                        if (predicate.test(new Pair<>(next, direction)))
                        {
                            BlockApiCache<Storage<ItemVariant>, Direction> cache = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, next);
                            output.add(new RetrievalTarget(cache, direction.getOpposite()));
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }
        return output;
    }

    public static <T extends Entity> T closestEntity(List<T> entityList, Vec3d pos)
    {
        T out;
        if (entityList.isEmpty())
        {
            return null;
        }
        else
        {
            out = entityList.get(0);
        }
        for (T entity : entityList)
        {
            double distance = pos.squaredDistanceTo(out.getX(), out.getY(), out.getZ());
            if (pos.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) < distance)
            {
                out = entity;
            }
        }
        return out;
    }
}
