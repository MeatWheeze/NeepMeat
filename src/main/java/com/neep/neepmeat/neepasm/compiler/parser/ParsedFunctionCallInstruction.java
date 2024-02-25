package com.neep.neepmeat.neepasm.compiler.parser;

import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedFunction;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import net.minecraft.server.world.ServerWorld;

public class ParsedFunctionCallInstruction implements ParsedInstruction
{
    private final String name;

    public ParsedFunctionCallInstruction(String name, TokenView view)
    {
        this.name = name;
    }

    @Override
    public void build(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
    {
        ParsedFunction function = parsedSource.findFunction(name);
        if (function == null)
            throw new NeepASM.CompilationException("function '" + name + "' does not exist");

        function.call(world, parsedSource, program);
    }
}
