package com.neep.neepmeat.neepasm.compiler.parser;

import com.neep.neepmeat.api.plc.instruction.CallInstruction;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.instruction.JumpInstruction;
import net.minecraft.server.world.ServerWorld;

public class CallInstructionParser implements InstructionParser
{
    @Override
    public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        String label = view.nextIdentifier();

        if (label.isEmpty())
            throw new NeepASM.ParseException("expected label");

        view.fastForward();
        if (!parser.isComment(view) && !view.lineEnded())
            throw new NeepASM.ParseException("unexpected token '" + view.nextBlob() + "'");

        return (world, source, program) ->
        {
            Label label1 = source.findLabel(label);
            if (label1 == null)
                throw new NeepASM.CompilationException("label '" + label + "' does not exist");

            program.addBack(new CallInstruction(label1));
        };
    }
}
