package com.neep.neepmeat.neepasm.compiler.parser;

import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.instruction.Instruction;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Function;

public class JumpInstructionParser implements InstructionParser
{
    private final Function<Label, Instruction> constructor;

    public JumpInstructionParser(Function<Label, Instruction> constructor)
    {
        this.constructor = constructor;
    }

    @Override
    public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();

        String label = view.nextIdentifier();

        if (!label.isEmpty())
        {
            parser.assureLineEnd(view);

            return new ParsedJumpInstruction(constructor, label);
        }

        if (TokenView.isDigit(view.peek()))
        {
            int jump = view.nextInteger();

            parser.assureLineEnd(view);

            return new ParsedRelJumpInstruction(constructor, jump);
        }
        throw new NeepASM.ParseException("expected label or relative jump");
    }

    public static class ParsedJumpInstruction implements ParsedInstruction
    {
        private final Function<Label, Instruction> constructor;
        private final String label;

        public ParsedJumpInstruction(Function<Label, Instruction> constructor, String label)
        {
            this.constructor = constructor;
            this.label = label;
        }

        @Override
        public void build(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
        {
            Label l = parsedSource.findLabel(label);
            if (l == null)
                throw new NeepASM.CompilationException("label '" + label + "' does not exist");

            program.addBack(constructor.apply(l));
        }
    }

    public static class ParsedRelJumpInstruction implements ParsedInstruction
    {
        private final Function<Label, Instruction> constructor;
        private final int jump;

        public ParsedRelJumpInstruction(Function<Label, Instruction> constructor, int jump)
        {
            this.constructor = constructor;
            this.jump = jump;
        }

        @Override
        public void build(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
        {
            Label l = new Label("relative", program.size() + jump);

            program.addBack(constructor.apply(l));
        }
    }
}
