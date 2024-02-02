package com.neep.neepmeat.neepasm.compiler.parser;

import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;

public interface InstructionParser
{
    ParsedInstruction parse(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException;
}
