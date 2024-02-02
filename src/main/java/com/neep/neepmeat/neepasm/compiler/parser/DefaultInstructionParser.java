package com.neep.neepmeat.neepasm.compiler.parser;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedInstructionInstruction;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;

import java.util.List;

public class DefaultInstructionParser implements InstructionParser
{
    private final InstructionProvider provider;

    public DefaultInstructionParser(InstructionProvider provider)
    {
        this.provider = provider;
    }

    @Override
    public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        List<Argument> arguments = Lists.newArrayList();
        List<KeyValue> kvs = Lists.newArrayList();

        boolean read = true;
        while (read)
        {
            Argument a;
            KeyValue kv;
            view.fastForward();
            if (parser.isComment(view) || view.lineEnded())
            {
                read = false;
            }
            else if ((a = parser.parseArgument(view)) != null)
            {
                arguments.add(a);
            }
            else if ((kv = parser.parseKV(view)) != null)
            {
                kvs.add(kv);
            }
            else
            {
                throw new NeepASM.ParseException("unexpected token", view.peek());
            }
            view.fastForward();
        }

        return new ParsedInstructionInstruction(provider, arguments, kvs);
    }
}
