package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;

import java.util.EnumMap;

public class MachineBlock extends BaseBlock implements LivingMachineStructure
{
    private final EnumMap<Property, Float> properties = new EnumMap<>(Property.class);

    public MachineBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
        properties.put(Property.MAX_POWER, 10f);
    }

    @Override
    public EnumMap<Property, Float> getProperties()
    {
        return properties;
    }
}
