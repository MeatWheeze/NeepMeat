package com.neep.neepmeat.plc;

import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;

public interface PLCState
{
    void setInstructionBuilder(InstructionProvider provider);
    void argument(Argument argument);

    RecordMode getMode();
}
