package com.neep.neepbus.block;

import com.neep.neepbus.util.NeepBusPort;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * To be implemented by blocks.
 * Not using BlockApiLookup so that it is possible to check for a NeepBusProvider without calling World::getBlockEntity.
 */
public interface NeepBusProvider
{
    Map<String, NeepBusPort> NO_PORTS = Map.of();

//    BlockApiLookup<NeepBusMember, Direction> LOOO = BlockApiLookup.get(
//            new Identifier(NeepMeat.NAMESPACE, "neepbus_member"), NeepBusMember.class, Direction.class);

    Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state);

    void networkChanged(World world, BlockPos pos, BlockPos whereChanged);
}
