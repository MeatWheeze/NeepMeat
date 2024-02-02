package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.neepasm.program.Program;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ParsedMacro
{
    private final List<ParsedInstruction> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();

    public void instruction(ParsedInstruction instruction)
    {
        instructions.add(instruction);
    }

    public void label(Label label)
    {
        labels.add(label);
    }

    public int size()
    {
        return instructions.size();
    }

//    public  build(ServerWorld world, ParsedSource parsedSource, Program program) throws NeepASM.CompilationException
//    {
//        for (PreInstruction preInstruction : parsedSource.instructions())
//        {
//            try
//            {
//            }
//            catch (NeepASM.CompilationException e)
//            {
//                // TODO: line information in parsed source
//                throw new NeepASM.ProgramBuildException(0, 0, e.getMessage());
//            }
//        }
//        return null;
//    }

    public void expand(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
    {
//        for (Label label : labels)
//        {
//        }

        // Append the instructions
        for (ParsedInstruction instruction : instructions)
        {
            instruction.build(world, parsedSource, program);
        }
    }
}
