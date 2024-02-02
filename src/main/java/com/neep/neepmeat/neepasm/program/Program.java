package com.neep.neepmeat.neepasm.program;

import com.neep.neepmeat.plc.instruction.Instruction;

public interface Program
{
    Instruction get(int index);

    int size();
}
