package com.neep.neepmeat.api.live_machine;

import java.util.EnumMap;

public interface LivingMachineStructure
{
    EnumMap<StructureProperty, StructureProperty.Entry> getProperties();
}
