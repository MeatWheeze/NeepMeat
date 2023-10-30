package com.neep.neepmeat.transport.interfaces;

import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.transport.blood_network.BloodNetworkManager;
import com.neep.neepmeat.transport.data.PipeNetworkSerialiser;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.item_network.ItemNetworkImpl;

public interface IServerWorld
{

    default FluidNodeManager getFluidNodeManager()
    {
        return null;
    }

    default void setFluidNetworkManager(PipeNetworkSerialiser manager)
    {

    }

    default PipeNetworkSerialiser getPipeNetworkManager()
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

    default BloodNetworkManager getBloodNetworkManager() { return null; }
}
