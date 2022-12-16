package com.neep.neepmeat.api.machine;

import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.util.math.Direction;

public interface IMotorisedBlock
{
    boolean tick(IMotorBlockEntity motor);

    void setWorkMultiplier(float multiplier);

    default boolean canConnect(Direction direction)
    {
        return true;
    }

    default void onMotorRemoved() {};
}
