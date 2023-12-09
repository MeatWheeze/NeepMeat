package com.neep.neepmeat.api.plc.instruction;

public interface InstructionBuilder
{
    InstructionBuilder argument(Argument argument) throws InstructionException;
    boolean isComplete();
    Instruction build();
}
