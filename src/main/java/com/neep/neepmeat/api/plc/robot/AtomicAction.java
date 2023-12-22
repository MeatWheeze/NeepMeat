package com.neep.neepmeat.api.plc.robot;

import com.neep.neepmeat.api.plc.PLC;

import java.util.function.Consumer;

/**
 * Represents an action that is completed in a single tick. It does not need to hold and serialise an internal state.
 */
public interface AtomicAction extends RobotAction
{
    static AtomicAction of(Consumer<PLC> action)
    {
        return action::accept;
    }

    @Override
    default boolean finished(PLC plc)
    {
        return true;
    }

    @Override
    default void tick(PLC plc)
    {
    }
}
