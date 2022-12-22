package com.neep.neepmeat.transport.util;

import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.machine.item.ItemPumpBlock;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class ItemPipeUtil
{
    /** Handles the movement of an {@link ItemInPipe} to an adjacent block.
     */
    // TODO: Cutoff depth
    public static long pipeToAny(ItemInPipe item, BlockPos pos, Direction out, World world, TransactionContext transaction, boolean simpleCheck)
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

    /** Ejects the entire contents of the given storage into a pipe network of the world.
     * @return Whether more than zero items were successfully ejected
     */
    public static boolean storageToAny(ServerWorld world, Storage<ItemVariant> storage, BlockPos pos, Direction facing, TransactionContext transaction)
    {
        for (StorageView<ItemVariant> view : storage.iterable(transaction))
        {
            try (Transaction inner = transaction.openNested())
            {
                if (view.isResourceBlank())
                {
                    inner.abort();
                    continue;
                }

                long ejected = stackToAny(world, pos, facing, view.getResource(), view.getAmount(), inner);
                long extracted = view.extract(view.getResource(), ejected, inner);
                if (ejected == extracted)
                {
                    inner.commit();
                    return ejected != 0;
                }
                else inner.abort();
            }
        }
        return false;
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

    /** Spawns an item entity in the world with a given speed.
     */
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

    /** Handles transfer of an {@link ItemInPipe} into a {@link IItemPipe}
     */
    public static long itemToPipe(ItemInPipe item, IItemPipe pipe, World world, BlockPos toPos, BlockState toState, Direction out, boolean simpleCheck, TransactionContext transaction)
    {
        long amountInserted = 0;
//        if (IItemPipe.isConnectedIn(world, pos, state, out))
        if (pipe.canItemEnter(item.toResourceAmount(), world, toPos, toState, out.getOpposite()))
        {
            if (simpleCheck)
            {
                long simpleAmount = canEjectSimple(item.toResourceAmount(), world, toPos, out, transaction);
                item.setAmount((int) simpleAmount);
            }
            if (item.amount() != 0)
                amountInserted = pipe.insert(world, toPos, toState, out.getOpposite(), item, transaction);
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

    /** Handles ejection of items into the world, taking into account pipes and storage implementations.
     */
    public static int stackToAny(ServerWorld world, BlockPos pos, Direction facing, ItemVariant variant, long amount, TransactionContext transaction)
    {
        if (variant.isBlank() || amount == 0) return 0;

        BlockPos offset = pos.offset(facing);
        BlockState facingState = world.getBlockState(offset);
        Storage<ItemVariant> storage;
        long transferred = 0;
        try (Transaction nested = transaction.openNested())
        {
            if (facingState.isAir())
            {
                transferred = itemToWorld(variant.toStack((int) amount), 0.5, 0.05f, world, offset, facing, nested);
                nested.commit();
            }
            else if (facingState.getBlock() instanceof IItemPipe itemPipe)
            {
                transferred = itemToPipe(new ItemInPipe(new ResourceAmount<>(variant, amount), world.getTime()), itemPipe, world, offset, facingState, facing, true, nested);
                nested.commit();
            }
            else if ((storage = ItemStorage.SIDED.find(world, offset, facing)) != null)
            {
                transferred = storage.insert(variant, amount, nested);
                nested.commit();
            }
            else nested.abort();
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
        Queue<Direction> dirQueue = new LinkedList<>();
        // TODO: Use a HashSet
        Set<Long> visited = new HashSet<>(); // Hopefully using longs will speed up comparison
        queue.add(startPipe);
        pipeQueue.add((IItemPipe) world.getBlockState(startPipe).getBlock());
        dirQueue.add(exit.getOpposite());
        visited.add(startPipe.offset(exit.getOpposite()).asLong());

        while (!queue.isEmpty())
        {
            BlockPos current = queue.poll();
            IItemPipe currentPipe = pipeQueue.poll();
            Direction currentDir = dirQueue.poll();
            BlockState currentState = world.getBlockState(current);

            visited.add(current.asLong());

            if (!currentPipe.singleOutput() && currentPipe.getConnections(currentState, v -> true).size() > 2)
            {
                return item.amount();
            }

            for (Direction direction : Direction.values())
            {
                if (direction == currentDir) continue;

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
                        dirQueue.add(direction.getOpposite());
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

    // TODO: Fix
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
}