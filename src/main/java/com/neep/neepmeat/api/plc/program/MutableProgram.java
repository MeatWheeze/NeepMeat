package com.neep.neepmeat.api.plc.program;

import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.instruction.Instruction;

public interface MutableProgram extends PLCProgram
{

    void addBack(Instruction instruction);

    void insert(int index, Instruction instruction);

    void remove(int index);

    void add(int selected, Instruction instruction);

    void setDebugLine(int line);
}
