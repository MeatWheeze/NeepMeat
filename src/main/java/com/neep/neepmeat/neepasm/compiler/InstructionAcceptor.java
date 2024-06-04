package com.neep.neepmeat.neepasm.compiler;

import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.program.Label;

public interface InstructionAcceptor
{
    void instruction(ParsedInstruction preInstruction, int line);
    void label(Label label);
    int size();
}
