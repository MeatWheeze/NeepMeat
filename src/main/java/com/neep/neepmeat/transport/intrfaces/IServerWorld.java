package com.neep.neepmeat.transport.intrfaces;

import com.neep.neepmeat.transport.data.FluidNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;

public interface IServerWorld
{

    default FluidNodeManager getFluidNodeManager()
    {
        return null;
    }

    default void setFluidNetworkManager(FluidNetworkManager manager)
    {

    }

    default FluidNetworkManager getFluidNetworkManager()
    {
        return null;
    }
}
