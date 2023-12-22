package com.neep.neepmeat.transport.util;

import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class TubeUtils
{
    /** Handles the movement of an item from a pipe to an adjacent block.
     */
    // TODO: Cutoff depth
    public static long tryTransfer(ItemInPipe item, BlockPos pos, BlockState state, Direction out, World world)
    {
        BlockPos toPos = pos.offset(out);
        BlockState toState = world.getBlockState(toPos);
        Block toBlock = toState.getBlock();

        long amountInserted = 0;
        Storage<ItemVariant> storage;
        if (toBlock instanceof IItemPipe pipe)
        {
            if (IItemPipe.isConnectedIn(world, pos, state, out))
//            if (pipe.getConnections(state1, IItemPipe::all).contains(item.out))
            {
//                if (insert(item, world, toState, toPos, item.out.getOpposite()) > 0)
                amountInserted = pipe.insert(world, toPos, toState, out.getOpposite(), item);
            }
        }
        else if (toState.isAir())
        {
            double offset = 0.2;
            ItemEntity itemEntity = new ItemEntity(world,
                    toPos.getX() + 0.5 - offset * out.getOffsetX(),
                    toPos.getY() + 0.1 - offset * out.getOffsetY(),
                    toPos.getZ() + 0.5 - offset * out.getOffsetZ(),
                    item.getItemStack().copy(),
                    out.getOffsetX() * item.speed, out.getOffsetY() * item.speed, out.getOffsetZ() * item.speed);
            world.spawnEntity(itemEntity);
            return item.getCount();
        }
        else if ((storage = ItemStorage.SIDED.find(world, toPos, out.getOpposite())) != null)
        {
            amountInserted = pipeToStorage(item, storage);
        }

        // TODO: is this condition necessary?
        if (amountInserted != item.getCount())
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
    public static long pipeToStorage(ItemInPipe item, Storage<ItemVariant> storage)
    {
        Transaction t = Transaction.openOuter();
        long transferred = storage.insert(item.getResourceAmount().resource(), item.getCount(), t);
        if (transferred > 0)
        {
            t.commit();
            if (transferred == item.getCount())
            {
//                item.decrement((int) transferred);
                return item.getCount();
            }
            item.decrement((int) transferred);
            return transferred;
        }
        t.abort();
        return 0;
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
    public static int ejectStack(ServerWorld world, BlockPos pos, Direction facing, ItemStack stack)
    {
        if (stack.isEmpty()) return 0;

        BlockPos offset = pos.offset(facing);
        BlockState facingState = world.getBlockState(offset);
        Storage<ItemVariant> storage;
        if (facingState.isAir())
        {
            double x = pos.getX() + facing.getOffsetX() * 0.5 + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + facing.getOffsetZ() * 0.5 + 0.5;
            ItemEntity entity = new ItemEntity(world, x, y, z, stack, facing.getOffsetX() * 0.05, 0, facing.getOffsetZ() * 0.05);
            world.spawnEntity(entity);
            return stack.getCount();
        }
        else if (facingState.getBlock() instanceof IItemPipe itemPipe)
        {
            return (int) itemPipe.insert(world, offset, facingState, facing, new ItemInPipe(stack, world.getTime()));
        }
        else if ((storage = ItemStorage.SIDED.find(world, offset, facing)) != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                long amount = stack.getCount();
                long inserted = storage.insert(ItemVariant.of(stack), amount, transaction);
                transaction.commit();
                return (int) inserted;
            }
        }
        return 0;
    }
}
