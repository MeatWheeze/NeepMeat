package com.neep.neepmeat.api.live_machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.neep.neepmeat.api.processing.PowerUtils;

import java.util.EnumMap;

public enum StructureProperty implements Formatter
{
    FAULT_TOLERANCE(0, StructurePropertyFormatter.DEFAULT_FLOAT_FORMAT::format),
    SPEED(1, StructurePropertyFormatter.DEFAULT_FLOAT_FORMAT::format),
    SELF_REPAIR(0, StructurePropertyFormatter::formatRepair), // Format in % per second
    MAX_POWER(0, v -> PowerUtils.POWER_FORMAT.format(v) + PowerUtils.POWER_UNIT.getString()),
    MASS(1, StructurePropertyFormatter.DEFAULT_FLOAT_FORMAT::format),
    ;

    public static final Codec<StructureProperty> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("ordinal").forGetter(StructureProperty::ordinal))
                    .apply(instance, id -> StructureProperty.values()[id]));

    public static final EnumMap<StructureProperty, PropertyValue> EMPTY = new EnumMap<>(StructureProperty.class);

    private final float def;
    private final StructurePropertyFormatter formatter;

    StructureProperty(float def, StructurePropertyFormatter formatter)
    {
        this.def = def;
        this.formatter = formatter;
    }

    public float defaultValue()
    {
        return def;
    }

    public String format(PropertyValue value)
    {
        return formatter.format(value.value());
    }
}
