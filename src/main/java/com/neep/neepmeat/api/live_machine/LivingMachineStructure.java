package com.neep.neepmeat.api.live_machine;

import java.util.EnumMap;

public interface LivingMachineStructure
{
    EnumMap<Property, Float> getProperties();

    enum Property
    {
        FAULT_TOLERANCE,
        EFFICIENCY,
        SELF_REPAIR,
        MAX_POWER
    }
}
