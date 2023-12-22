package com.neep.assembly.block;

import com.neep.neepmeat.util.LinearDirection;
import net.minecraft.state.property.EnumProperty;

public interface IRail
{
    EnumProperty<LinearDirection> DIRECTION = EnumProperty.of("direction", LinearDirection.class);
}
