package com.neep.neepmeat.api.machine;

import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.util.PowerUtils;
import net.minecraft.util.math.Direction;

public interface IMotorisedBlock
{
    boolean tick(IMotorBlockEntity motor);

    void setInputPower(float power);

    default boolean canConnect(Direction direction)
    {
        return true;
    }

    default void onMotorRemoved() {};

    default float getLoadTorque() { return PowerUtils.MOTOR_TORQUE_LOSS; }
}
