package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.plc.Instructions;
import org.jetbrains.annotations.NotNull;

public abstract class ComparisonInstruction implements Instruction
{
    @Override
    public abstract void start(PLC plc);

    @Override
    public abstract @NotNull InstructionProvider getProvider();

    // Not sure how to reduce the boilerplate here.
    public static class Equals extends ComparisonInstruction
    {
        @Override
        public void start(PLC plc)
        {
//            Variable<?> v2 = plc.variableStack().peek(0);;
//            Variable<?> v1 = plc.variableStack().peek(1);
//
//            if (v1.notEmpty() && v1.equals(v2))
//                plc.flag(1);
//            else
//                plc.flag(0);
//
            if (plc.variableStack().peekInt(1) == plc.variableStack().peekInt(0))
                plc.flag(1);
            else
                plc.flag(0);
            plc.advanceCounter();
        }

        @Override
        public @NotNull InstructionProvider getProvider() { return Instructions.EQ; }
    }

    public static class LessThan extends ComparisonInstruction
    {
        @Override
        public void start(PLC plc)
        {
//            Variable<?> v2 = plc.variableStack().peek(0);;
//            Variable<?> v1 = plc.variableStack().peek(1);
//
//            if (v1.notEmpty() && v1.type().equals(v2.type()))
//            {
//                int cmp = v1.compare(v2);
//                if (cmp != -2)
//                {
//                    plc.flag(cmp == -1 ? 1 : 0);
//                    plc.advanceCounter();
//                    return;
//                }
//            }
//            plc.raiseError(new PLC.Error("Different variable types or invalid comparison"));
            if (plc.variableStack().peekInt(1) < plc.variableStack().peekInt(0))
                plc.flag(1);
            else
                plc.flag(0);
            plc.advanceCounter();
        }

        @Override
        public @NotNull InstructionProvider getProvider() { return Instructions.LT; }
    }

    public static class LessThanEqual extends ComparisonInstruction
    {
        @Override
        public void start(PLC plc)
        {
//            Variable<?> v2 = plc.variableStack().peek(0);;
//            Variable<?> v1 = plc.variableStack().peek(1);
//
//            if (v1.notEmpty() && v1.type().equals(v2.type()))
//            {
//                int cmp = v1.compare(v2);
//                if (cmp != -2)
//                {
//                    plc.flag(cmp == -1 || cmp == 0 ? 1 : 0);
//                    plc.advanceCounter();
//                    return;
//                }
//            }
//            plc.raiseError(new PLC.Error("Different variable types or invalid comparison"));
            if (plc.variableStack().peekInt(1) <= plc.variableStack().peekInt(0))
                plc.flag(1);
            else
                plc.flag(0);
            plc.advanceCounter();
        }

        @Override
        public @NotNull InstructionProvider getProvider() { return Instructions.LTEQ; }
    }

    public static class GreaterThan extends ComparisonInstruction
    {
        @Override
        public void start(PLC plc)
        {
//            Variable<?> v2 = plc.variableStack().peek(0);;
//            Variable<?> v1 = plc.variableStack().peek(1);
//
//            if (v1.notEmpty() && v1.type().equals(v2.type()))
//            {
//                int cmp = v1.compare(v2);
//                if (cmp != -2)
//                {
//                    plc.flag(cmp == 1 ? 1 : 0);
//                    plc.advanceCounter();
//                    return;
//                }
//            }
//            plc.raiseError(new PLC.Error("Different variable types or invalid comparison"));
            if (plc.variableStack().peekInt(1) > plc.variableStack().peekInt(0))
                plc.flag(1);
            else
                plc.flag(0);
            plc.advanceCounter();

        }

        @Override
        public @NotNull InstructionProvider getProvider() { return Instructions.GT; }
    }

    public static class GreaterThanEqual extends ComparisonInstruction
    {
        @Override
        public void start(PLC plc)
        {
//            Variable<?> v2 = plc.variableStack().peek(0);
//            Variable<?> v1 = plc.variableStack().peek(1);
//
//            if (v1.notEmpty() && v1.type().equals(v2.type()))
//            {
//                int cmp = v1.compare(v2);
//                if (cmp != -2)
//                {
//                    plc.flag(cmp == 1 || cmp == 0 ? 1 : 0);
//                    plc.advanceCounter();
//                    return;
//                }
//            }
//            plc.raiseError(new PLC.Error("Different variable types or invalid comparison"));
            if (plc.variableStack().peekInt(1) >= plc.variableStack().peekInt(0))
                plc.flag(1);
            else
                plc.flag(0);
            plc.advanceCounter();
        }

        @Override
        public @NotNull InstructionProvider getProvider() { return Instructions.GTEQ; }
    }
}
