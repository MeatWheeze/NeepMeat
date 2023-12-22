package com.neep.neepmeat.plc.program;

import com.neep.neepmeat.plc.instruction.Instruction;

public interface MutableProgram extends PlcProgram
{
    void addBack(Instruction instruction);

    void insert(int index, Instruction instruction);

    void remove(int index);
}
