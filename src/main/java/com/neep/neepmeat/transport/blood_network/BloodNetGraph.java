package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.Set;

public class BloodNetGraph
{
    protected final World world;
    protected Long2ObjectOpenHashMap<VascularConduitEntity> conduits = new Long2ObjectOpenHashMap<>();
    protected Set<BloodAcceptor> acceptors = Sets.newHashSet();

    public BloodNetGraph(World world)
    {
        this.world = world;
    }

    public void clear()
    {
        conduits.values().forEach(c -> c.setNetwork(null));

        conduits.clear();
        acceptors.clear();
    }

    public Long2ObjectMap<VascularConduitEntity> getConduits()
    {
        return conduits;
    }

    public Set<BloodAcceptor> getAcceptors()
    {
        return acceptors;
    }

    // TODO: Find acceptors
    public void rebuild(BlockPos startPos)
    {
        clear();

        Set<Long> visited = Sets.newHashSet();
        Queue<BlockPos> posQueue = Queues.newArrayDeque();
        BlockPos.Mutable mutable = startPos.mutableCopy();

        posQueue.add(startPos);
        visited.add(startPos.asLong());

        while (!posQueue.isEmpty())
        {
            var current = posQueue.poll();
            var currentState = world.getBlockState(current);

            var conduit = VascularConduit.find(world, current, currentState);
            if (conduit != null)
            {
                for (Direction direction : Direction.values())
                {
                    if (!conduit.isConnectedIn(world, current, currentState, direction)) continue;

                    mutable.set(current, direction);

                    if (visited.contains(mutable.asLong())) continue;
                    visited.add(mutable.asLong());

                    BlockState nextState = world.getBlockState(mutable);
                    VascularConduit nextConduit = VascularConduit.find(world, mutable, nextState);
                    if (nextConduit.isConnectedIn(world, mutable, nextState, direction.getOpposite()))
                    {
                        posQueue.add(mutable.toImmutable());
//                        conduits.put(mutable.asLong(), VascularConduitEntity.find(world, mutable));
                        insert(mutable.asLong(), conduit.getEntity(world, mutable, nextState));
                    }
                }
            }
        }
    }

    public boolean isEmpty()
    {
        return conduits.isEmpty();
    }

    public void insert(long pos, VascularConduitEntity newPart)
    {
        conduits.put(pos, newPart);
    }
}
