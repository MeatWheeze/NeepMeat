package com.neep.neepbus.block;

import com.neep.neepbus.block.entity.ConfigProvider;
import com.neep.neepbus.util.NeepBusConfig;
import com.neep.neepbus.util.PortMap;
import com.neep.neepbus.util.WritePort;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * To be implemented by blocks.
 * Not using BlockApiLookup so that it is possible to check for a NeepBusProvider without calling World::getBlockEntity.
 */
public interface NeepBusProvider
{
    PortMap NO_PORTS = PortMap.EMPTY;

//    BlockApiLookup<NeepBusMember, Direction> LOOO = BlockApiLookup.get(
//            new Identifier(NeepMeat.NAMESPACE, "neepbus_member"), NeepBusMember.class, Direction.class);

    default PortMap getPorts(World world, BlockPos pos, BlockState state)
    {
        NeepBusConfig config = getConfig(world, pos, state);
        if (config != null)
            return config.getPorts();

        return PortMap.EMPTY;
    }

    @Nullable
    default NeepBusConfig getConfig(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof ConfigProvider provider)
        {
            return provider.getConfig();
        }
        return null;
    }

    void networkChanged(World world, BlockPos pos, BlockPos whereChanged);
}
