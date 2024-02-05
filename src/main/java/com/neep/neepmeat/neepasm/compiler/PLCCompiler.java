package com.neep.neepmeat.neepasm.compiler;

import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.api.plc.program.PLCProgram;
import com.neep.neepmeat.api.storage.WorldSupplier;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.program.PLCProgramImpl;

public class PLCCompiler
{
    private final WorldSupplier world;

    public PLCCompiler(WorldSupplier world)
    {
        this.world = world;
    }

    public PLCProgram compile(ParsedSource parsedSource) throws NeepASM.ProgramBuildException
    {
        MutableProgram program = new PLCProgramImpl(world.as());

        for (ParsedInstruction preInstruction : parsedSource.instructions())
        {
            try
            {
                preInstruction.build(world.get(), parsedSource, program);
            }
            catch (NeepASM.CompilationException e)
            {
                // TODO: line information in parsed source
                throw new NeepASM.ProgramBuildException(0, 0, e.getMessage());
            }
        }

//        for (ParsedMacro macro : parsedSource.macros())
//        {
//            try
//            {
//                macro.build(world.get(), parsedSource, program);
//            }
//            catch (NeepASM.CompilationException e)
//            {
//                throw new NeepASM.ProgramBuildException(0, 0, e.getMessage());
//            }
//        }

        return program;
    }
}
