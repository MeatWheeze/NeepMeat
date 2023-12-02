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

    void advanceCounter(int increment);

    void raiseError(Error error);
    
    class Error
    {
        private final Text what;

        public Error(Text what)
        {
            this.what = what;
        }

        public Error(String what)
        {
            this.what = Text.of(what);
        }

        public Text what()
        {
            return what;
        }
    }
}
