package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;
import com.neep.neepmeat.api.live_machine.StructureProperty;

import java.util.EnumMap;
import java.util.Map;

public class MachineBlock extends BaseBlock implements LivingMachineStructure
{
    private final EnumMap<StructureProperty, StructureProperty.Entry> properties;

    public MachineBlock(String registryName, Map<StructureProperty, StructureProperty.Entry> properties, Settings settings)
    {
        super(registryName, settings);
        this.properties = new EnumMap<>(properties);
//        properties.put(Property.MAX_POWER, 10f);
    }

    @Override
    public EnumMap<StructureProperty, StructureProperty.Entry> getProperties()
    {
        return properties;
    }
}
