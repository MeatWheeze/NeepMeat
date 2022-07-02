package com.neep.neepmeat.item_transfer;

import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TubeUtils
{
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
                amountInserted = ((IItemPipe) toBlock).insert(world, toPos, toState, out.getOpposite(), item);
            }
        }
        else if (toState.isAir())
        {
            double offset = 0.2;
            Entity itemEntity = new ItemEntity(world,
                    toPos.getX() + 0.5 - offset * out.getOffsetX(),
                    toPos.getY() + 0.1 - offset * out.getOffsetY(),
                    toPos.getZ() + 0.5 - offset * out.getOffsetZ(),
                    item.getItemStack(),
                    out.getOffsetX() * item.speed, out.getOffsetY() * item.speed, out.getOffsetZ() * item.speed);
            world.spawnEntity(itemEntity);
            amountInserted = item.getCount();
        }
        else if ((storage = ItemStorage.SIDED.find(world, toPos, out)) != null)
        {
            amountInserted = pipeToStorage(item, storage);
        }
        if (amountInserted == item.getCount())
        {
            return -1;
        }
        else
        {
            item.getItemStack().decrement((int) amountInserted);
            bounce(item, world, state);
            return amountInserted;
        }
    }

    public static long pipeToStorage(ItemInPipe item, Storage<ItemVariant> storage)
    {
        Transaction t = Transaction.openOuter();
        long transferred = storage.insert(item.getResourceAmount().resource(), item.getResourceAmount().amount(), t);
        if (transferred > 0)
        {
            t.commit();
            if (transferred == item.getItemStack().getCount())
            {
                return -1;
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
}
