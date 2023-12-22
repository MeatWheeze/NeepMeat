package com.neep.neepmeat.transport.api.pipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.blood_network.BloodNetworkManager;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public interface VascularConduit
{
    BlockApiLookup<VascularConduit, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "vascular_conduit"),
            VascularConduit.class, Void.class);

    static VascularConduit find(World world, BlockPos pos, BlockState state)
    {
        if (state instanceof VascularConduit conduit)
            return conduit;

        return LOOKUP.find(world, pos, null);
    }

    default void updatePosition(World world, BlockPos pos, BlockState state, VascularConduitEntity.UpdateReason reason)
    {
        if (world.isClient())
            return;

        switch (reason)
        {
            case ADDED ->
            {
                List<BloodNetwork> adjNetworks = Lists.newArrayList();
                BlockPos.Mutable mutable = pos.mutableCopy();
                for (Direction direction : Direction.values())
                {
                    if (!isConnectedIn(world, pos, state, direction)) continue;

                    mutable.set(pos, direction);
                    var network = BloodNetwork.find(world, mutable);
                    if (network != null)
                    {
                        adjNetworks.add(network);
                    }
                }

                if (adjNetworks.isEmpty())
                {
                    var network = BloodNetworkManager.get(world).create(pos);
                    network.add(pos, getEntity(world, pos, state));
                }
                else if (adjNetworks.size() == 1)
                {
                    adjNetworks.get(0).add(pos, getEntity(world, pos, state));
                }
                else
                {
                    adjNetworks.get(0).add(pos, getEntity(world, pos, state));
                    adjNetworks.get(0).merge(adjNetworks);
                }
            }
            case REMOVED ->
            {
                Set<BloodNetwork> updatedNetworks = Sets.newHashSet();
                BlockPos.Mutable mutable = pos.mutableCopy();
                for (Direction direction : Direction.values())
                {
                    if (!isConnectedIn(world, pos, state, direction)) continue;

                    mutable.set(pos, direction);
                    var network = BloodNetwork.find(world, mutable);
                    if (network != null && !updatedNetworks.contains(network))
                    {
                        updatedNetworks.add(network);
                        network.update(mutable.toImmutable(), reason);
                    }
                    else
                    {
                        // If there is a connected pipe with a null network, it must have been disowned.
                        BloodNetworkManager.get(world).create(mutable);
                    }
                }
            }
            case CHANGED ->
            {

            }
        }
    }

    default boolean isConnectedIn(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof AbstractPipeBlock)
        {
            return state.get(AbstractPipeBlock.DIR_TO_CONNECTION.get(direction)).isConnected();
        }
        return false;
    }

    VascularConduitEntity getEntity(World world, BlockPos pos, BlockState state);
}
