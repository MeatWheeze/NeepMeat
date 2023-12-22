package com.neep.neepmeat.api.plc.robot;

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
