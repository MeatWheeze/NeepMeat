package com.neep.neepmeat.machine.integrator;

@FunctionalInterface
public interface DataResourceStorage
{
    long extract(long maxAmount);
}
