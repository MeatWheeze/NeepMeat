package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Queues;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.stream.Stream;

public class BloodNetworkImpl implements BloodNetwork
{
    protected final ServerWorld world;

    protected final BloodNetGraph conduits;

    protected AcceptorManager acceptors = new AcceptorManager();

    protected Queue<BloodAcceptor> sinkUpdateQueue = Queues.newArrayDeque();

    protected float output;
    protected boolean removed = false;

    public BloodNetworkImpl(ServerWorld world)
    {
        this.world = world;
        this.conduits = new BloodNetGraph(world, acceptors);
    }


    public void sort()
    {
//        sources.clear();
//        sinks.clear();
//        acceptors.stream().forEach(acceptor ->
//        {
//            if (acceptor.getMode() == BloodAcceptor.Mode.OUT)
//            {
//                sources.add(acceptor);
//            }
//            else
//            {
//                sinks.add(acceptor);
//            }
//        });
    }

    protected boolean validate()
    {
        if (conduits.isEmpty())
        {
            conduits.clear();
            acceptors.clear();
            removed = true;
            return false;
        }
        return true;
    }

    @Override
    public void rebuild(BlockPos pos, VascularConduitEntity.UpdateReason reason)
    {
        // Disown all conduits
        conduits.getConduits().values().forEach(c -> c.setNetwork(null));
        acceptors.stream().forEach(a -> a.updateInflux(0));
        acceptors.clear();

        // Rediscover conduits and acceptors, leaving disconnected branches with null network.
        conduits.rebuild(pos);

        if (!validate()) return;

        conduits.getConduits().values().forEach(c -> c.setNetwork(this));
        sort();
    }

    @Override
    public void tick()
    {
        float internal = 0;

        if (acceptors.sort)
            acceptors.sort();

        var it = acceptors.sources().iterator();
        while (it.hasNext())
        {
            var acceptor = it.next();
            internal += acceptor.getRate();
        }

        float out = internal / acceptors.sinks().asList().size();
        while (!sinkUpdateQueue.isEmpty())
        {
            var sink = sinkUpdateQueue.poll();

            sink.updateInflux(out);
        }
    }

    @Override
    public void add(BlockPos pos, VascularConduitEntity newPart)
    {
        insert(pos.asLong(), newPart);
    }

    @Override
    public void insert(long pos, VascularConduitEntity newPart)
    {
        conduits.insert(pos, newPart); // Acceptors will be detected automatically
        newPart.setNetwork(this);

        sinkUpdateQueue.addAll(acceptors.sinks().asList());
    }

    @Override
    public void remove(BlockPos pos, VascularConduitEntity part)
    {
        acceptors.get(pos).forEach(a -> a.updateInflux(0));
        acceptors.remove(pos);

        conduits.remove(pos.asLong());

        sinkUpdateQueue.addAll(acceptors.sinks().asList());

        validate();
    }

    @Override
    public void update(BlockPos pos, VascularConduitEntity part)
    {
        acceptors.get(pos).forEach(a -> a.updateInflux(0));

        acceptors.remove(pos);
        conduits.remove(pos.asLong());
        conduits.insert(pos.asLong(), part);

        sinkUpdateQueue.addAll(acceptors.sinks().asList());

        validate();
    }

    @Override
    public void addAcceptor(long pos, BloodAcceptor acceptor)
    {
        acceptors.add(pos, acceptor);
    }


    public void mergeInto(BloodNetwork network)
    {
        if (network instanceof BloodNetworkImpl other)
        {
            conduits.getConduits().forEach(other::insert);
            acceptors.mergeInto(other.acceptors);

            conduits.clear();
            acceptors.clear();
            removed = true;
        }
        else throw new NotImplementedException();
    }

    public void merge(List<BloodNetwork> adjNetworks)
    {
        for (var network : adjNetworks)
        {
            if (network == this) continue;

            network.mergeInto(this);
        }
        acceptors.sort();
        sinkUpdateQueue.addAll(acceptors.sinks().asList());
    }

    public boolean isRemoved()
    {
        return removed;
    }

    static class AcceptorManager
    {
        protected Long2ObjectMultimap<BloodAcceptor> acceptors = new Long2ObjectMultimap<>();
        protected Long2ObjectMultimap<BloodAcceptor> sources = new Long2ObjectMultimap<>();
        protected Long2ObjectMultimap<BloodAcceptor> sinks = new Long2ObjectMultimap<>();
        public boolean sort = false;

        public Collection<BloodAcceptor> get(BlockPos pos)
        {
            var set = acceptors.get(pos.asLong());

            if (set == null)
                return Collections.emptySet();

            return set;
        }

        public Stream<BloodAcceptor> stream()
        {
            return acceptors.flatStream();
        }

        public void mergeInto(AcceptorManager other)
        {
            acceptors.fastForEach(entry ->
            {
                // Create an entry in the other map if not present and merge acceptor sets
                other.acceptors.get(entry.getLongKey()).addAll(entry.getValue());
            });
            clear();
            other.sort = true;
        }

        public void add(long pos, BloodAcceptor acceptor)
        {
            acceptors.put(pos, acceptor);
            if (acceptor.getMode().isOut())
            {
                sources.put(pos, acceptor);
            }
            else
            {
                sinks.put(pos, acceptor);
            }
        }

        public void sort()
        {
            sources.clear();
            sinks.clear();
//            acceptors.stream().forEach(acceptor ->
            acceptors.fastForEach(entry ->
            {
                entry.getValue().forEach(acceptor ->
                {
                    if (acceptor.getMode().isOut())
                    {
                        sources.put(entry.getLongKey(), acceptor);
                    }
                    else
                    {
                        sinks.put(entry.getLongKey(), acceptor);
                    }
                });

            });
//            sinkUpdateQueue.addAll(sinks); // Anything could have just happened, so everything nees updating.
            sort = false;
        }

        public void remove(BlockPos pos)
        {
//            acceptors.get(pos.asLong()).forEach(acceptor ->
//            {
//                sources.remove(acceptor);
//                sinks.remove(acceptor);
//            });

            long lpos = pos.asLong();

            acceptors.remove(lpos);
            sources.remove(lpos);
            sinks.remove(lpos);
        }

        public void clear()
        {
            acceptors.clear();
            sources.clear();
            sinks.clear();
        }

        public Stream<BloodAcceptor> sources()
        {
            return sources.flatStream();
        }

        public Long2ObjectMultimap<BloodAcceptor> sinks()
        {
            return sinks;
        }
    }
}
