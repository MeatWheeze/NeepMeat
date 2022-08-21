package com.neep.neepmeat.blockentity.integrator;

@FunctionalInterface
public interface DataResourceStorage
{
    long extract(long maxAmount);
}
