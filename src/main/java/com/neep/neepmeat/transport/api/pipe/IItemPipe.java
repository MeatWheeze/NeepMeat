package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public interface IItemPipe
{
    static boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        else if (state.getBlock() instanceof IItemPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    default List<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        List<Direction> set = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            if (state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected()
            && forbidden.test(direction))
            {
                set.add(direction);
            }
        }
        return set;
    }

    long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction);

    default boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return true;
    }

    default boolean canItemEnter(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction inFace)
    {
        return isConnectedIn(world, pos, state, inFace);
    }

    default boolean canItemLeave(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction outFace)
    {
        return isConnectedIn(world, pos, state, outFace);
    }

    static boolean all(Direction direction)
    {
        return true;
    }

    default Direction getOutputDirection(ItemInPipe item, BlockState state, World world, Direction in)
    {
        List<Direction> connections = ((IItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

        Direction out = item.getPreferredOutputDirection(state, in, this);
        if (out != null && connections.contains(out)) return out;

        var rand = world.getRandom();
        if (!connections.isEmpty())
        {
            out = connections.get(rand.nextInt(connections.size()));
        }
        else
        {
            out = in;
        }
        return out;
    }

    default boolean singleOutput()
    {
        return true;
    }

    default boolean routeAware()
    {
        return false;
    }

    default void onAdded(BlockPos pos, BlockState state, ServerWorld world)
    {
        ((IServerWorld) world).getItemNetwork().onPipeAdded(this, pos, state);
    }

    default void onBroken(BlockPos pos, ServerWorld world)
    {
        ((IServerWorld) world).getItemNetwork().onPipeRemove(pos);
    }
}
