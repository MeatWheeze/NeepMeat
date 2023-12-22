package com.neep.neepmeat.api.plc.robot;

public interface RobotAction
{
    boolean finished();

    void start();

    void tick();

    default boolean blocksController() { return true; }

    default void cancel() {};
}
