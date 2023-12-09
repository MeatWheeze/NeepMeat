package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;

public interface InstructionBuilder
{
    InstructionBuilder argument(Argument argument) throws InstructionException;
    boolean isComplete();
    Instruction build();

    int argumentCount();
}
