package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.plc.PLC;

public interface ImmediateInstruction
{
    void argument(Argument argument, PLC plc);

    void interrupt(PLC plc);
}
