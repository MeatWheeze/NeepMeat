package com.neep.neepmeat.fluid_util;

import net.minecraft.state.property.EnumProperty;

public class PipeProperties
{
    public static final EnumProperty<PipeConnectionType> NORTH_CONNECTION = EnumProperty.of("north", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> SOUTH_CONNECTION = EnumProperty.of("south", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> EAST_CONNECTION = EnumProperty.of("east", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> WEST_CONNECTION = EnumProperty.of("west", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> UP_CONNECTION = EnumProperty.of("up", PipeConnectionType.class);
    public static final EnumProperty<PipeConnectionType> DOWN_CONNECTION = EnumProperty.of("down", PipeConnectionType.class);
}
