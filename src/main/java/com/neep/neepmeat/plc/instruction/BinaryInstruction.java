package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BinaryInstruction implements Instruction
{
    private final Supplier<InstructionProvider> provider;
    private final BinaryOperation operation;

    public BinaryInstruction(Supplier<InstructionProvider> provider, BinaryOperation operation)
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
        return provider.get();
    }
    
    @FunctionalInterface
    public interface BinaryOperation
    {
        int apply(int first, int last);
    }
}
