package com.neep.neepmeat.fluid_util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class FluidParticipant extends SnapshotParticipant<ResourceAmount<FluidVariant>>
{

    FluidVariant resource;

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot()
    {
//        return new ResourceAmount<FluidVariant>(resource, amount);
        return null;
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot)
    {

    }
}
