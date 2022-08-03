package com.neep.neepmeat.block.machine;

import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.util.math.Direction;

public interface IMotorisedBlock
{
    void tick(IMotorBlockEntity motor);

    void setWorkMultiplier(float multiplier);

    default boolean canConnect(Direction direction)
    {
        return true;
    }
}
