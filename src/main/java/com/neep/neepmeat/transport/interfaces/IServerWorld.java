package com.neep.neepmeat.transport.interfaces;

import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.transport.blood_network.BloodNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.item_network.PipeCacheImpl;

public interface IServerWorld
{

    default FluidNodeManager getFluidNodeManager()
    {
        return null;
    }

    default PipeCacheImpl getItemNetwork()
    {
        return null;
    }

    default EnlightenmentEventManager getEnlightenmentEventManager()
    {
        return null;
    }

    default BloodNetworkManager getBloodNetworkManager() { return null; }
}
