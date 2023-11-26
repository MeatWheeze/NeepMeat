package com.neep.neepmeat.plc.program;

import com.neep.meatlib.util.NbtSerialisable;

public interface PlcProgram extends NbtSerialisable
{
    PLCInstruction get(int index);

    int size();


}