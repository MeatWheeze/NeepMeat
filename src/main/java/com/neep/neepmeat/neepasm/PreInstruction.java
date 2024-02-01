package com.neep.neepmeat.neepasm;

import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;

import java.util.List;

public class PreInstruction
{
    private final InstructionProvider provider;
    private final List<Argument> arguments;
    private final List<KeyValue> kvs;

    public PreInstruction(InstructionProvider provider, List<Argument> arguments, List<KeyValue> kvs)
    {
        this.provider = provider;
        this.arguments = arguments;
        this.kvs = kvs;
    }

    public Instruction build()
    {
        return null;
    }
}
