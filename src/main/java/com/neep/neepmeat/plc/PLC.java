package com.neep.neepmeat.plc;

import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.plc.robot.RobotAction;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public interface PLC
{
    RobotAction addRobotAction(RobotAction action, Consumer<PLC> callback);

    SurgicalRobot getRobot();

    void advanceCounter();

    void raiseError(Error error);
    
    class Error
    {
        private final Text what;

        public Error(Text what)
        {
            this.what = what;
        }

        public Text what()
        {
            return what;
        }
    }
}
