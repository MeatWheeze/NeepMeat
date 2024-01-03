package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBloodAcceptor implements BloodAcceptor
{
    @Nullable protected BloodNetwork network;

    @Override
    public void setNetwork(BloodNetwork network)
    {
        this.network = network;
    }

    @Nullable
    public BloodNetwork getNetwork()
    {
        return network;
    }
}
