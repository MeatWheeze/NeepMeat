package com.neep.neepmeat.transport.util;

import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class TubeUtils
{
    /** Handles the movement of an item from a pipe to an adjacent block.
     */
    // TODO: Cutoff depth
    public static long pipeToAny(ItemInPipe item, BlockPos pos, BlockState state, Direction out, World world, TransactionContext transaction, boolean simpleCheck)
    {
        BlockPos toPos = pos.offset(out);
        BlockState toState = world.getBlockState(toPos);
        Block toBlock = toState.getBlock();

        long amountInserted = 0;
        Storage<ItemVariant> storage;
        if (toBlock instanceof IItemPipe pipe)
        {
            amountInserted = itemToPipe(item, pipe, world, toPos, toState, out, simpleCheck, transaction);
        }
        else if (toState.isAir())
        {
            amountInserted = itemToWorld(item.getItemStack(), 0.2, item.speed, world, toPos, out, transaction);
        }
        else if ((storage = ItemStorage.SIDED.find(world, toPos, out.getOpposite())) != null)
        {
            amountInserted = itemToStorage(item, storage, transaction);
        }

        // TODO: is this condition necessary?
        if (amountInserted != item.getAmount())
        {
        item.decrement((int) amountInserted);
        }

        return amountInserted;
    }

    /**
     * @param item Pipe item
     * @param storage The ItemVariant storage to be inserted into
     * @return The number of items that were inserted
     */
    public static long itemToStorage(ItemInPipe item, Storage<ItemVariant> storage, TransactionContext transaction)
    {
        try (Transaction nested = transaction.openNested())
        {
            long transferred = storage.insert(item.toResourceAmount().resource(), item.getAmount(), nested);
            if (transferred > 0)
            {
                nested.commit();
                item.decrement((int) transferred);
                return transferred;
            }
            nested.abort();
        }
        return 0;
    }

    public static long itemToWorld(ItemStack item, double offset, float speed, World world, BlockPos toPos, Direction out, TransactionContext transaction)
    {
        transaction.addOuterCloseCallback(r ->
        {
            ItemEntity itemEntity = new ItemEntity(world,
                    toPos.getX() + 0.5 - offset * out.getOffsetX(),
                    toPos.getY() + 0.2 - offset * out.getOffsetY(),
                    toPos.getZ() + 0.5 - offset * out.getOffsetZ(),
                    item.copy(),
                    out.getOffsetX() * speed, out.getOffsetY() * speed, out.getOffsetZ() * speed);
            world.spawnEntity(itemEntity);
        });
        return item.getCount();
    }

    public static long itemToPipe(ItemInPipe item, IItemPipe pipe, World world, BlockPos toPos, BlockState toState, Direction out, boolean simpleCheck, TransactionContext transaction)
    {
        long amountInserted = 0;
//        if (IItemPipe.isConnectedIn(world, pos, state, out))
        if (pipe.canItemEnter(item.toResourceAmount(), world, toPos, toState, out.getOpposite()))
        {
            if (simpleCheck)
            {
                long simpleAmount = canEjectSimple(item.toResourceAmount(), world, toPos, out, transaction);
                ItemInPipe newItem = item.copyWith((int) simpleAmount);
                amountInserted = pipe.insert(world, toPos, toState, out.getOpposite(), newItem, transaction);
            }
            else amountInserted = pipe.insert(world, toPos, toState, out.getOpposite(), item, transaction);
        }
        return amountInserted;
    }

    public static void bounce(ItemInPipe item, World world, BlockState state)
    {
        Direction out;
        Direction in = item.out;

        List<Direction> connections = ((IItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

        Random rand = world.getRandom();
        if (!connections.isEmpty())
        {
            out = connections.get(rand.nextInt(connections.size()));
        }
        else
        {
            out = in;
        }

        item.reset(in, out, world.getTime());
    }

    /** Handles ejection of an ItemStack into the world, taking into account pipes and storage implementations.
     */
    public static int stackToAny(ServerWorld world, BlockPos pos, Direction facing, ItemStack stack)
    {
        if (stack.isEmpty()) return 0;

        BlockPos offset = pos.offset(facing);
        BlockState facingState = world.getBlockState(offset);
        Storage<ItemVariant> storage;
        long transferred = 0;
        try (Transaction transaction = Transaction.openOuter())
        {
            if (facingState.isAir())
            {
                transferred = itemToWorld(stack, 0.5, 0.05f, world, offset, facing, transaction);
                transaction.commit();
            }
            else if (facingState.getBlock() instanceof IItemPipe itemPipe)
            {
                transferred = itemToPipe(new ItemInPipe(stack, world.getTime()), itemPipe, world, offset, facingState, facing, true, transaction);
                transaction.commit();
            }
            else if ((storage = ItemStorage.SIDED.find(world, offset, facing)) != null)
            {
                long amount = stack.getCount();
                transferred = storage.insert(ItemVariant.of(stack), amount, transaction);
                transaction.commit();
            }
        }
        return (int) transferred;
    }

    /** Simple route detection that does not take branches into account.
     * @return Number of items that can be safely transferred. Takes max value if a branch is met.
     */
    public static long canEjectSimple(ResourceAmount<ItemVariant> item, World world, BlockPos startPipe, Direction exit, @Nullable TransactionContext transaction)
    {
        Queue<BlockPos> queue = new LinkedList<>();
        Queue<IItemPipe> pipeQueue = new LinkedList<>();
        List<Long> visited = new ArrayList<>(); // Hopefully using longs will speed up comparison
        queue.add(startPipe);
        pipeQueue.add((IItemPipe) world.getBlockState(startPipe).getBlock());
        visited.add(startPipe.offset(exit.getOpposite()).asLong());

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();
            IItemPipe currentPipe = pipeQueue.poll();
            BlockState currentState = world.getBlockState(current);

            visited.add(current.asLong());

            if (!currentPipe.singleOutput() && currentPipe.getConnections(currentState, v -> true).size() > 2)
            {
                return item.amount();
            }

            for (Direction direction : Direction.values())
            {
                BlockPos offset = current.offset(direction);
                BlockState offsetState = world.getBlockState(offset);
                if (currentPipe.canItemLeave(item, world, current, currentState, direction))
                {
                    if (offsetState.isAir()) return item.amount();

                    Storage<ItemVariant> storage;
                    if (offsetState.getBlock() instanceof IItemPipe pipe
                                    && pipe.canItemEnter(item, world, offset, offsetState, direction.getOpposite())
                                    && !visited.contains(offset.asLong()))
                    {
                        queue.add(offset);
                        pipeQueue.add(pipe);
                    }
                    else if ((storage = ItemStorage.SIDED.find(world, offset, offsetState, null, direction.getOpposite())) != null)
                    {
                        return storage.simulateInsert(item.resource(), item.amount(), transaction);
                    }
                }
            }
        }
        return 0;
    }
}
