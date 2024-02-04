package com.neep.neepmeat.api.plc;

import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.plc.robot.PLCActuator;
import it.unimi.dsi.fastutil.Stack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface PLC
{
    void addRobotAction(RobotAction action, Consumer<PLC> callback);

    PLCActuator getActuator();

    /**
     * @param pos Position to look for actuator. Pass null to select the default actuator.
     */
    void selectActuator(@Nullable BlockPos pos);

    int counter();

    void advanceCounter();
    void pushCall(int data);

    int popCall();

    Stack<Variable<?>> variableStack();

    void setCounter(int counter);

    void raiseError(Error error);
    void flag(int i);

    int flag();

    interface PLCProvider
    {
        PLC get();
    }

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
