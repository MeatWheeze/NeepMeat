package com.neep.neepmeat.transport.blood_network;

import com.google.common.collect.Sets;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

public class BloodNetworkImpl implements BloodNetwork
{
    protected final ServerWorld world;

    protected final BloodNetGraph conduits;

    protected AcceptorManager acceptors = new AcceptorManager();

//    protected Queue<BloodAcceptor> sinkUpdateQueue = Queues.newArrayDeque();
    protected LinkedHashSet<BloodAcceptor> sinkUpdateQueue = Sets.newLinkedHashSet();

    protected float output;
    protected boolean removed = false;

    public BloodNetworkImpl(ServerWorld world)
    {
        this.world = world;
        this.conduits = new BloodNetGraph(world, acceptors);
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
        sinkUpdateQueue.clear();

        // Rediscover conduits and acceptors, leaving disconnected branches with null network.
        conduits.rebuild(pos);

        if (!validate()) return;

        conduits.getConduits().values().forEach(c -> c.setNetwork(this));
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

        var sit = sinkUpdateQueue.iterator();
        while (sit.hasNext())
        {
            var sink = sit.next();
            sit.remove();

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
        acceptors.get(pos).forEach(a -> { if (a != null) a.updateInflux(0); } );
        acceptors.remove(pos);

        conduits.remove(pos.asLong());

        sinkUpdateQueue.addAll(acceptors.sinks().asList());

        validate();
    }

    @Override
    public void update(BlockPos pos, VascularConduitEntity part)
    {
        acceptors.get(pos).forEach(a -> { if (a != null) a.updateInflux(0); } );

        acceptors.remove(pos);
        conduits.remove(pos.asLong());
        conduits.insert(pos.asLong(), part);

        sinkUpdateQueue.addAll(acceptors.sinks().asList());

        validate();
    }

//    @Override
//    public void addAcceptor(long pos, int dir, BloodAcceptor acceptor)
//    {
//        acceptors.add(pos, dir, acceptor);
//    }


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

    public NbtCompound toNbt()
    {
        NbtCompound root = new NbtCompound();

        root.put("conduits", conduits.toNbt());

        return root;
    }

//    static class AcceptorReference
//    {
//        private @Nullable BloodAcceptor acceptor;
//
//        public @Nullable BloodAcceptor get()
//        {
//            return acceptor;
//        }
//    }

    class AcceptorManager
    {
        protected PosDirectionMap<BloodAcceptor> acceptors = new PosDirectionMap<>(BloodAcceptor.class);
        protected PosDirectionMap<BloodAcceptor> sources = new PosDirectionMap<>(BloodAcceptor.class);
        protected PosDirectionMap<BloodAcceptor> sinks = new PosDirectionMap<>(BloodAcceptor.class);
        public boolean sort = false;


        public void discover(World world, BlockPos pos, BlockState state, VascularConduit conduit)
        {
            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Direction direction : Direction.values())
            {
                if (!conduit.isConnectedIn(world, pos, state, direction)) continue;

                mutable.set(pos, direction);

                BloodAcceptor acceptor = BloodAcceptor.SIDED.find(world, mutable, direction.getOpposite());
                if (acceptor != null)
                {
                    add(pos.asLong(), direction.getId(), acceptor);
                    sinkUpdateQueue.add(acceptor);
                }
            }
        }

        public Iterable<BloodAcceptor> get(BlockPos pos)
        {
            return acceptors.get(pos.asLong());
        }

        public Stream<BloodAcceptor> stream()
        {
            return acceptors.flatStream();
        }

        public void mergeInto(AcceptorManager other)
        {
            acceptors.fastForEach(entry ->
            {
                for (int dir = 0; dir < 6; ++dir)
                {
                    var a = entry.getValue();
                    other.acceptors.put(entry.getLongKey(), dir, a[dir]);
                }
            });
            clear();
            other.sort = true;
        }

        public void add(long pos, int dir, BloodAcceptor acceptor)
        {
            acceptors.put(pos, dir, acceptor);
            if (acceptor.getMode().isOut())
            {
                sources.put(pos, dir, acceptor);
            }
            else
            {
                sinks.put(pos, dir, acceptor);
            }
        }

        public void sort()
        {
            sources.clear();
            sinks.clear();
//            acceptors.stream().forEach(acceptor ->
            acceptors.fastForEach(entry ->
            {
                for (int dir = 0; dir < 6; ++dir)
                {
                    var acceptor = entry.getValue()[dir];
                    if (acceptor == null) continue;

                    if (acceptor.getMode().isOut())
                    {
                        sources.put(entry.getLongKey(), dir, acceptor);
                    }
                    else
                    {
                        sinks.put(entry.getLongKey(), dir, acceptor);
                    }
                }
            });
            sort = false;
        }

        public void remove(BlockPos pos)
        {
            long lpos = pos.asLong();

            acceptors.remove(lpos);
            sources.remove(lpos);
            sinks.remove(lpos);

//            for (var sink : sinks.get(lpos))
//            {
//
//            }
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

        public PosDirectionMap<BloodAcceptor> sinks()
        {
            return sinks;
        }

        public NbtCompound toNbt()
        {
            NbtCompound root = new NbtCompound();

//            var list = new NbtList();

            acceptors.fastForEach(entry ->
            {
//                list.add()
            });

            return root;
        }
    }
}
