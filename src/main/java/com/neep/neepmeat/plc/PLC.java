package com.neep.neepmeat.plc;

import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.plc.robot.RobotAction;

import java.util.function.Consumer;

public interface PLC
{
    RobotAction addRobotAction(RobotAction action, Consumer<PLC> callback);

    SurgicalRobot getRobot();

    void advanceCounter();

    void raiseError();
}
