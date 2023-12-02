package com.neep.neepmeat.plc.robot;

import com.neep.neepmeat.plc.program.CombineInstruction;

public interface SingleAction extends RobotAction
{
    static SingleAction of(Runnable action)
    {
        return action::run;
    }

    @Override
    default boolean finished()
    {
        return true;
    }

    @Override
    default void tick()
    {
    }
}
