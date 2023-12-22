package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class ResourceSnapshotParticipant<T> extends SnapshotParticipant<ResourceAmount<T>>
{

    Supplier<T> getResource;
    Supplier<Long> getAmount;
    Consumer<T> setResource;
    Consumer<Long> setAmount;

    public ResourceSnapshotParticipant(Supplier<T> getResource, Supplier<Long> getAmount,
                                       Consumer<T> setResource, Consumer<Long> setAmount)
    {
        this.getResource = getResource;
        this.getAmount = getAmount;
        this.setResource = setResource;
        this.setAmount = setAmount;
    }

    @Override
    protected ResourceAmount<T> createSnapshot()
    {
        return new ResourceAmount<>(getResource.get(), getAmount.get());
    }

    @Override
    protected void readSnapshot(ResourceAmount<T> snapshot)
    {
        setResource.accept(snapshot.resource());
        setAmount.accept(snapshot.amount());
    }
}
