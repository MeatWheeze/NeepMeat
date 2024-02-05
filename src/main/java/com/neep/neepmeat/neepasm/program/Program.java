package com.neep.neepmeat.neepasm.program;

import com.neep.neepmeat.plc.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

public interface Program
{
    Instruction get(int index);

    int size();

    int getDebugLine(int counter);
}
