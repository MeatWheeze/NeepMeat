package com.neep.neepmeat.transport.blood_network;

import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BloodNetworkImpl implements BloodNetwork
{
    protected final ServerWorld world;

    protected final BloodNetGraph conduits;
    protected Set<BloodAcceptor> sources = new HashSet<>();
    protected Set<BloodAcceptor> sinks = new HashSet<>();

    protected float output;
    protected boolean removed;

    public BloodNetworkImpl(ServerWorld world)
    {
        this.world = world;
        this.conduits = new BloodNetGraph(world);
    }

    public void sort()
    {
        sources.clear();
        sinks.clear();
        for (var acceptor : conduits.getAcceptors())
        {
            if (acceptor.getMode() == BloodAcceptor.Mode.OUT)
            {
                sources.add(acceptor);
            }
            else
            {
                sinks.add(acceptor);
            }
        }
    }

    protected boolean validate()
    {
        if (conduits.isEmpty())
        {
            conduits.clear();
            sources.clear();
            sinks.clear();
            removed = true;
        }
        return true;
    }

    @Override
    public void update(BlockPos pos, VascularConduitEntity.UpdateReason reason)
    {
        conduits.rebuild(pos);

        if (!validate()) return;

        conduits.getConduits().values().forEach(c -> c.setNetwork(this));
        sort();
    }

    @Override
    public void tick()
    {
        float internal = 0;

        for (var acceptor : sources)
        {
            internal += acceptor.getRate();
        }

        if (internal / sinks.size() != output)
        {
            output = internal / sinks.size();
            for (var acceptor : sinks)
            {
                acceptor.updateInflux(output);
            }
        }
    }

    public void add(BlockPos pos, VascularConduitEntity newPart)
    {
        add(pos.asLong(), newPart);
    }

    public void add(long pos, VascularConduitEntity newPart)
    {
        conduits.insert(pos, newPart);
        newPart.setNetwork(this);
    }

    public void mergeInto(BloodNetwork other)
    {
        conduits.getConduits().forEach(other::add);

        conduits.clear();
        sources.clear();
        sinks.clear();
        removed = true;
    }

    public void merge(List<BloodNetwork> adjNetworks)
    {
        for (var network : adjNetworks)
        {
            if (network == this) continue;

            network.mergeInto(this);
        }
        sort();
    }

    public boolean isRemoved()
    {
        return removed;
    }
}
