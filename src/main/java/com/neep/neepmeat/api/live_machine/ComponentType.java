package com.neep.neepmeat.api.live_machine;

public interface ComponentType<T extends LivingMachineComponent>
{
    class Simple<T extends LivingMachineComponent> implements ComponentType<T>
    {

    }
}
