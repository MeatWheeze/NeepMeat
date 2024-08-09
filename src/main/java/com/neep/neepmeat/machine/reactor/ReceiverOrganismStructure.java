package com.neep.neepmeat.machine.reactor;

import com.neep.neepmeat.api.live_machine.Formatter;
import com.neep.neepmeat.api.live_machine.PropertyValue;
import com.neep.neepmeat.api.live_machine.StructurePropertyFormatter;

import java.util.EnumMap;

public interface ReceiverOrganismStructure
{
    EnumMap<Property, PropertyValue> getProperties();

    enum Property implements Formatter
    {
        ORGANISATION(0.1f, StructurePropertyFormatter.DEFAULT_FLOAT_FORMAT::format);

        private final float defaultValue;
        private final StructurePropertyFormatter formatter;

        @Override
        public String format(PropertyValue value)
        {
            return formatter.format(value.value());
        }

        public float defaultValue()
        {
            return defaultValue;
        }

        Property(float defaultValue, StructurePropertyFormatter formatter)
        {
            this.defaultValue = defaultValue;
            this.formatter = formatter;
        }
    }
}
