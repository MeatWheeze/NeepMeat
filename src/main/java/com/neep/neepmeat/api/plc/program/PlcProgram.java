package com.neep.neepmeat.api.plc.program;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.instruction.Instruction;

public interface PlcProgram extends NbtSerialisable
{
    Instruction get(int index);

    int size();


}