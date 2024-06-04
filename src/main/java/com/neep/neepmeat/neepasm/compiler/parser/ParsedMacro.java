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

    private int numExpansions;

    public ParsedMacro(String name, List<String> parameters, String macroText, int startLine)
    {
        this.name = name;
        this.parameters = parameters;
        this.macroText = macroText;
        this.startLine = startLine;
    }

    public void expand(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        // Give each expansion a unique name.
        String localName = name + "#" + numExpansions;
        numExpansions++;

        // Skip over macro name
        view.nextIdentifier();

        List<String> arguments = Lists.newArrayList();
        for (int i = 0; i < parameters.size(); ++i)
        {
            view.fastForward();

            String arg = parseArgumentString(view, parser);
            if (arg.isEmpty())
                throw new NeepASM.ParseException("not enough comma-separated macro arguments.");

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
            parseLine(macroView, parsedSource, parser, localName);
            macroView.nextLine();
            line++;
        }
    }

    private void parseLine(TokenView view, ParsedSource parsedSource, Parser parser, String localName) throws NeepASM.ParseException
    {
        String token;
        char follow;
        try (var entry = view.save())
        {
            token = view.nextIdentifier();
            follow = view.nextThing();
            if (follow == ':')
            {
                // Use the mangled label name here to prevent duplicate labels after repeated expansions.
                parsedSource.label(new Label(ParsedSource.mangleLabel(token, localName), parsedSource.size()));
                view.fastForward();
                parser.assureLineEnd(view);

                entry.commit();
            }
        }
        catch (NeepASM.ParseException e)
        {
            throw new RuntimeException(e);
        }

        ParsedInstruction instruction = parser.parseInstruction(view, localName);
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
