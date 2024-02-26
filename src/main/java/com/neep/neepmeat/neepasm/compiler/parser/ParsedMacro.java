package com.neep.neepmeat.neepasm.compiler.parser;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.program.Label;

import java.util.List;

// Stores the raw text of the macro allowing blind substitution of text arguments.
public class ParsedMacro
{
    private final String name;
    private final List<String> parameters;
    private final String macroText;
    private final int startLine;

    public ParsedMacro(String name, List<String> parameters, String macroText, int startLine)
    {
        this.name = name;
        this.parameters = parameters;
        this.macroText = macroText;
        this.startLine = startLine;
    }

    public void expand(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        // Skip over macro name
        view.nextIdentifier();

        List<String> arguments = Lists.newArrayList();
        for (int i = 0; i < parameters.size(); ++i)
        {
            view.fastForward();

            String arg = parseArgumentString(view, parser);
            if (arg.isEmpty())
                throw new NeepASM.ParseException("not enough macro arguments");

            if (!view.lineEnded())
                view.next();
            arguments.add(arg);
        }

        view.fastForward();
        if (!parser.isComment(view) && !view.lineEnded())
            throw new NeepASM.ParseException("too many macro arguments");

        // Substitute
        String processed = macroText;
        for (int i = 0; i < parameters.size(); ++i)
        {
            processed = processed.replace("%" + parameters.get(i), arguments.get(i));
        }

        int line = 0;
        TokenView macroView = new TokenView(processed);
        while (!macroView.eof())
        {
            parseLine(macroView, parsedSource, parser);
            macroView.nextLine();
            line++;
        }
    }

    private void parseLine(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        String token;
        char follow;
        try (var entry = view.save())
        {
            token = view.nextIdentifier();
            follow = view.nextThing();
            if (follow == ':')
            {
                parsedSource.label(new Label(token, parsedSource.size()));
                view.fastForward();
                parser.assureLineEnd(view);

                entry.commit();
            }
        }
        catch (NeepASM.ParseException e)
        {
            throw new RuntimeException(e);
        }

        ParsedInstruction instruction = parser.parseInstruction(view);
        if (instruction != null)
            parsedSource.instruction(instruction, startLine + view.line());
    }

    private String parseArgumentString(TokenView view, Parser parser)
    {
        view.fastForward();
        StringBuilder builder = new StringBuilder();
        while (view.peek() != ',' && !view.lineEnded() && !parser.isComment(view))
        {
            if (view.eof() || view.lineEnded())
                break;

            builder.append(view.next());
        }
        char c = view.peek();
        return builder.toString().strip();
    }

    public String name()
    {
        return name;
    }
}
