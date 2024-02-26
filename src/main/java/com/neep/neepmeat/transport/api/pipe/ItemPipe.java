package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Iterables;
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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface ItemPipe
{
    static boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
//        if (state.getBlock() instanceof AbstractPipeBlock)
//        {
//            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
//        }
        if (state.getBlock() instanceof ItemPipe acceptor)
        {
            return acceptor.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    default Set<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Set<Direction> set = new HashSet<>();
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
        Set<Direction> connections = ((ItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

        Direction out = item.getPreferredOutputDirection(state, in, this);
        if (out != null && connections.contains(out)) return out;

        var rand = world.getRandom();
        if (!connections.isEmpty())
        {
            out = Iterables.get(connections, rand.nextInt(connections.size()));
//            out = connections.get(rand.nextInt(connections.size()));
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

    default void onChanged(BlockPos pos, ServerWorld world)
    {
        ((IServerWorld) world).getItemNetwork().onPipeRemove(pos);
    }

    default boolean supportsRouting()
    {
        return false;
    }
}
