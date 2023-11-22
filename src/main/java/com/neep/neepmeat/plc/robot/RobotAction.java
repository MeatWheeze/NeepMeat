package com.neep.neepmeat.plc.robot;

public interface RobotAction
{
    boolean finished();

    void start();

    void tick();
}
