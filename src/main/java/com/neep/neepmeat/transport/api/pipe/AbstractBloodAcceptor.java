package com.neep.neepmeat.transport.api.pipe;

import com.neep.neepmeat.transport.blood_network.BloodTransferChangeListener;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBloodAcceptor implements BloodAcceptor
{
    @Nullable protected BloodTransferChangeListener network;

    @Override
    public void setChangeListener(@Nullable BloodTransferChangeListener network)
    {
        this.network = network;
    }

    @Nullable
    public BloodTransferChangeListener getNetwork()
    {
        return network;
    }
}
