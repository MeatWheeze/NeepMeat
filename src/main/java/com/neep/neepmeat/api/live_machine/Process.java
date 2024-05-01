package com.neep.neepmeat.api.live_machine;

import java.util.List;

public interface Process
{
    void serverTick(LivingMachineBlockEntity be);

    List<ComponentType<?>> getRequired();
}
