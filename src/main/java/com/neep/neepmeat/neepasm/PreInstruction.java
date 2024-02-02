package com.neep.neepmeat.neepasm;

import com.neep.neepmeat.api.plc.instruction.InstructionBuilder;
import com.neep.neepmeat.api.plc.instruction.InstructionException;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.neepasm.program.MutableProgram;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    public Instruction build(ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
    {
        try
        {
            AtomicReference<Instruction> instruction = new AtomicReference<>();
            InstructionBuilder builder = provider.start(null, instruction::set);
            for (var argument : arguments)
            {
                builder = builder.argument(argument);
            }

            for (var kv : kvs)
            {
                builder = builder.keyValue(kv);
            }

            if (instruction.get() != null)
            {
                return instruction.get();
            }
        }
        catch (InstructionException e)
        {
            throw new NeepASM.CompilationException("ooer");
        }
        return null;
    }
}
