package com.neep.neepmeat.api.machine;

import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.minecraft.util.math.Direction;

public interface MotorisedBlock
{
    boolean tick(MotorEntity motor);

    void setInputPower(float power);

    default boolean canConnect(Direction direction)
    {
        return true;
    }

    default void onMotorRemoved() {};

    default float getLoadTorque() { return PowerUtils.MOTOR_TORQUE_LOSS; }
}
