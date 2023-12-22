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
    protected final BloodNetworkImpl.AcceptorManager acceptors;
    protected Long2ObjectOpenHashMap<VascularConduitEntity> conduits = new Long2ObjectOpenHashMap<>();

    public BloodNetGraph(World world, BloodNetworkImpl.AcceptorManager acceptors)
    {
        this.world = world;
        this.acceptors = acceptors;
    }

    public void clear()
    {
        conduits.clear();
    }

    public Long2ObjectMap<VascularConduitEntity> getConduits()
    {
        return conduits;
    }


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
                insert(current.asLong(), conduit.getEntity(world, current, currentState));

                for (Direction direction : Direction.values())
                {
                    if (!conduit.isConnectedIn(world, current, currentState, direction)) continue;

                    mutable.set(current, direction);

                    if (visited.contains(mutable.asLong())) continue;
                    visited.add(mutable.asLong());

                    BlockState nextState = world.getBlockState(mutable);

                    VascularConduit nextConduit = VascularConduit.find(world, mutable, nextState);
                    if (nextConduit != null && nextConduit.isConnectedIn(world, mutable, nextState, direction.getOpposite()))
                    {
                        posQueue.add(mutable.toImmutable());
                    }
                }
            }
        }
    }

    public boolean isEmpty()
    {
        return conduits.isEmpty();
    }

    public void insert(long lpos, VascularConduitEntity newPart)
    {
        conduits.put(lpos, newPart);

        BlockPos pos = BlockPos.fromLong(lpos);
        BlockState state = world.getBlockState(pos);
        var conduit = VascularConduit.find(world, pos, world.getBlockState(pos));
        if (conduit == null) return;

        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values())
        {
            if (!conduit.isConnectedIn(world, pos, state, direction)) continue;

            mutable.set(pos, direction);

            BloodAcceptor acceptor = BloodAcceptor.SIDED.find(world, mutable, direction.getOpposite());
            if (acceptor != null)
            {
                acceptors.add(lpos, acceptor);
            }
        }
    }

    public void remove(long pos)
    {
        conduits.remove(pos);
    }
}
