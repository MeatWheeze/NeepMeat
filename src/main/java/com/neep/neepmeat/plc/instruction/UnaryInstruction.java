package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class UnaryInstruction implements Instruction
{
    private final Supplier<InstructionProvider> provider;
    private final UnaryOperation operation;

    public UnaryInstruction(Supplier<InstructionProvider> provider, UnaryOperation operation)
    {
        this.provider = provider;
        this.operation = operation;
    }

    @Override
    public void start(PLC plc)
    {
        int last = plc.variableStack().popInt();
        plc.variableStack().push(operation.apply(last));
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return provider.get();
    }

    @FunctionalInterface
    public interface UnaryOperation
    {
        int apply(int last);
    }
}
