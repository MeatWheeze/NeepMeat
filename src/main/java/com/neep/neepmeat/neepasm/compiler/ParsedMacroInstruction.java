package com.neep.neepmeat.neepasm.compiler;

import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import net.minecraft.server.world.ServerWorld;

public class ParsedMacroInstruction implements ParsedInstruction
{
    private final ParsedMacro macro;

    public ParsedMacroInstruction(ParsedMacro macro)
    {
        this.macro = macro;
    }

    @Override
    public void build(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
    {
        macro.expand(world, parsedSource, program);
    }
}
