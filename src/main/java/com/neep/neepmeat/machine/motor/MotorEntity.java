package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.api.machine.MotorisedBlock;
import org.jetbrains.annotations.Nullable;

public interface MotorEntity
{
    default void onRemoved()
    {
        MotorisedBlock motorised = getConnectedBlock();
        if (motorised != null)
        {
            motorised.setInputPower(0);
            motorised.onMotorRemoved();
        }
    }

    float getRotorAngle();

    float getSpeed();

    double getMechPUPower();

    @Nullable
    MotorisedBlock getConnectedBlock();
}
