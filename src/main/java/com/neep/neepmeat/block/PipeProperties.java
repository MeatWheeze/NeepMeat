package com.neep.neepmeat.block;

import net.minecraft.state.property.EnumProperty;

public class PipeProperties
{
    public static final EnumProperty<PipeConnection> NORTH_CONNECTION = EnumProperty.of("north", PipeConnection.class);
    public static final EnumProperty<PipeConnection> SOUTH_CONNECTION = EnumProperty.of("south", PipeConnection.class);
    public static final EnumProperty<PipeConnection> EAST_CONNECTION = EnumProperty.of("east", PipeConnection.class);
    public static final EnumProperty<PipeConnection> WEST_CONNECTION = EnumProperty.of("west", PipeConnection.class);
    public static final EnumProperty<PipeConnection> UP_CONNECTION = EnumProperty.of("up", PipeConnection.class);
    public static final EnumProperty<PipeConnection> DOWN_CONNECTION = EnumProperty.of("down", PipeConnection.class);
}
