package com.neep.neepmeat.neepasm.compiler.parser;

import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import net.minecraft.server.world.ServerWorld;

public interface ParsedInstruction
{
    void build(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException;
}
