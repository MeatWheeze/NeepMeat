package com.neep.neepmeat.plc;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;

public interface PLCState extends NbtSerialisable
{
    void setInstructionBuilder(InstructionProvider provider);
    void argument(Argument argument);

    RecordMode getMode();

    int getArgumentCount();

    int getMaxArguments();
}
