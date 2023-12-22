package com.neep.neepmeat.transport.interfaces;

import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.transport.item_network.ItemNetworkImpl;
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

    default ItemNetworkImpl getItemNetwork()
    {
        return null;
    }

    default EnlightenmentEventManager getEnlightenmentEventManager()
    {
        return null;
    }
}
