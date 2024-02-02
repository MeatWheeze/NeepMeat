package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;

public interface InstructionBuilder
{
    InstructionBuilder argument(Argument argument) throws InstructionException;

    InstructionBuilder keyValue(KeyValue kv) throws InstructionException;

    boolean isComplete();
    Instruction build();

    int argumentCount();
}
