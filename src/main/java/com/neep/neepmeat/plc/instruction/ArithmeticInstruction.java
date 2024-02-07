package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import org.jetbrains.annotations.NotNull;

public class ArithmeticInstruction implements Instruction
{
    private final InstructionProvider provider;
    private final Operation operation;

    public ArithmeticInstruction(InstructionProvider provider, Operation operation)
    {
        this.provider = provider;
        this.operation = operation;
    }

    @Override
    public void start(PLC plc)
    {
        int last = plc.variableStack().popInt();
        int first = plc.variableStack().popInt();
        plc.variableStack().push(operation.apply(first, last));
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return provider;
    }
    
    @FunctionalInterface
    public interface Operation
    {
        int apply(int first, int last);
    }
}
