package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.alias.ParsedAlias;
import com.neep.neepmeat.neepasm.compiler.alias.ParsedArgumentAlias;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedFunctionCallInstruction;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedMacro;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * It's the worst lexer/parser ever!
 * We don't need all those silly things like operator precedence, nested scopes blah blah blah. It's all bloat.
 * Abstract syntax tree? Never heard of her.
 */
public class Parser
{
    private final Map<String, InstructionProvider> instructionMap = Maps.newHashMap();

    private ParsedSource parsedSource;
    private int line = 0;

    public Parser()
    {
        Instructions.REGISTRY.forEach(provider ->
        {
            instructionMap.put(provider.getParseName().toLowerCase(), provider);
        });
    }

    public ParsedSource parse(String source) throws NeepASM.ProgramBuildException
    {
        parsedSource = new ParsedSource();

        TokenView view = new TokenView(source);

        try
        {
            this.line = 0;
            while (!view.eof())
            {
                parseLine(view);
                view.nextLine();
                this.line++;
            }

            parsedSource.instruction(((world, s, program) -> program.addBack(Instruction.EMPTY)), -1);

            for (var func : parsedSource.functions())
            {
                func.expand(parsedSource);
            }
        }
        catch (NeepASM.ParseException e)
        {
            throw new NeepASM.ProgramBuildException(view.line(), view.linePos(), e.getMessage());
        }
        return parsedSource;
    }

    public void parseLine(TokenView view) throws NeepASM.ParseException
    {
        if (view.peekThing() == '%')
        {
            view.next();
            String id = view.nextIdentifier();
            switch (id)
            {
                case "func" ->
                {
                    parseFunction(view);
                    return;
                }
                case "macro" ->
                {
                    parseMacro(view);
                    return;
                }
                case "alias" ->
                {
                    parseAlias(view);
                    return;
                }
            }
            throw new NeepASM.ParseException("unexpected directive '" + id + "'");
        }

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
                if (!view.lineEnded() && !isComment(view))
                {
                    throw new NeepASM.ParseException("unexpected token after '" + token + "'");
                }

                entry.commit();
            }
        }

        // Handle macro expansions and normal instructions differently
        ParsedMacro macro = parsedSource.findMacro(token);
        if (macro != null)
        {
            macro.expand(view, parsedSource, this);
        }
        else
        {
            ParsedInstruction instruction = parseInstruction(view);
            if (instruction != null)
                parsedSource.instruction(instruction, view.line());
        }
    }

    private void parseMacro(TokenView view) throws NeepASM.ParseException
    {
        StringBuilder macroText = new StringBuilder();
        String name = view.nextIdentifier();
        if (name.isEmpty())
            throw new NeepASM.ParseException("expected macro name");

        int startLine = view.line();

        List<String> parameters = Lists.newArrayList();
        view.fastForward();
        while (TokenView.isIdentifier(0, view.peek()))
        {
            parameters.add(parseMacroParameter(view));
            view.fastForward();
        }

        if (!view.lineEnded() && !isComment(view))
            throw new NeepASM.ParseException("unexpected token");

        while (!view.eof())
        {
            if (view.peek() == '%')
            {
                try (var entry = view.save())
                {
                    view.next();
                    String id = view.nextIdentifier();
                    if (id.equals("end"))
                    {
                        entry.commit();
                        parsedSource.macro(new ParsedMacro(name, parameters, macroText.toString(), startLine));
                        return;
                    }
                }
            }

            macroText.append(view.peek());
            view.next();
        }

        throw new NeepASM.ParseException("reached end of file while parsing macro '" + name + "'");
    }

    private String parseMacroParameter(TokenView view)
    {
        view.fastForward();
        String name = view.nextIdentifier();
        return name;
    }

    private void parseAlias(TokenView view) throws NeepASM.ParseException
    {
        String name = view.nextIdentifier();
        if (name.isEmpty())
            throw new NeepASM.ParseException("expected identifier after alias");

        if (view.nextThing() != '=')
            throw new NeepASM.ParseException("expected = after alias name");

        Argument argument;
        if ((argument = parseArgument(view)) != null)
        {
            parsedSource.alias(new ParsedArgumentAlias(name, argument));
            return;
        }

        throw new NeepASM.ParseException("invalid value for alias '" + name + "'");
    }

    private void parseFunction(TokenView view) throws NeepASM.ParseException
    {
        String name = view.nextIdentifier();
        if (name.isEmpty())
            throw new NeepASM.ParseException("expected function name");

        if (!view.lineEnded() && !isComment(view))
            throw new NeepASM.ParseException("unexpected token");

        view.nextLine();

        ParsedFunction function = new ParsedFunction(name);
        view.fastForward();
        while (view.peekThing() != '%')
        {
            parseFunctionLine(function, view);
            view.nextLine();

            if (view.eof())
                throw new NeepASM.ParseException("reached end of file while parsing function '" + name + "'");
        }
        view.next();
        if (!view.nextIdentifier().equals("end"))
            throw new NeepASM.ParseException("in '" + name + "': directives not allowed in function");

        parsedSource.function(function);
    }

    private void parseFunctionLine(ParsedFunction function, TokenView view) throws NeepASM.ParseException
    {
        String token;
        char follow;
        try (var entry = view.save())
        {
            token = view.nextIdentifier();
            follow = view.nextThing();
            if (follow == ':')
            {
                function.label(new Label(token, function.size()));
                view.fastForward();
                if (!view.lineEnded() && !isComment(view))
                {
                    throw new NeepASM.ParseException("unexpected token after '" + token + "'");
                }

                entry.commit();
            }
        }

        ParsedInstruction instruction = parseInstruction(view);
        if (instruction != null)
            function.instruction(instruction, view.line());
    }

    @Nullable
    public ParsedInstruction parseInstruction(TokenView view) throws NeepASM.ParseException
    {
        view.fastForward();
        if (view.lineEnded() || isComment(view))
            return null;

        String id = view.nextIdentifier();

        InstructionProvider provider = readInstruction(id);
        if (provider != null)
        {
            InstructionParser parser = provider.getParser();
            return parser.parse(view, parsedSource, this);
        }

        ParsedFunction function = parsedSource.findFunction(id);
        if (function != null)
            return new ParsedFunctionCallInstruction(id, view);

        throw new NeepASM.ParseException("unrecognised operation '" + id + "'");
    }

    @Nullable
    private InstructionProvider readInstruction(String id)
    {
        return instructionMap.get(id.toLowerCase());
    }

    @Nullable
    public Argument parseArgument(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            if (view.peekThing() == '$')
            {
                view.next();
                entry.commit();

                String name = view.nextIdentifier();
                if (name.isEmpty())
                    throw new NeepASM.ParseException("expected alias name");

                ParsedAlias alias = parsedSource.findAlias(name);
                if (alias == null)
                    throw new NeepASM.ParseException("alias '" + name + "' not found");

                if (alias.type() != ParsedAlias.Type.ARGUMENT)
                    throw new NeepASM.ParseException("alias '" + name + "' is not a world position");

                return ((ParsedArgumentAlias) alias).argument();
            }

            if (view.peekThing() == '@')
            {
                view.next();
                entry.commit();

                b1: if (view.nextThing() == '(')
                {
                    int x, y, z;
                    Direction direction = Direction.UP;

                    if (isDigit(view.peekThing()))
                        x = view.nextInteger();
                    else break b1;

                    if (isDigit(view.peekThing()))
                        y = view.nextInteger();
                    else break b1;

                    if (isDigit(view.peekThing()))
                        z = view.nextInteger();
                    else break b1;

                    // Direction is optional
                    if (Character.isAlphabetic(view.peekThing()))
                    {
                        direction = parseDirection(view);
                        if (direction == null)
                            direction = Direction.UP; // Unrecognised text will throw an exception
                    }

                    if (view.nextThing() == ')')
                    {
                        return new Argument(new BlockPos(x, y, z), direction);
                    }
                }
                throw new NeepASM.ParseException("malformed target\n Targets should be of the form '@(<x> <y> <z> <direction>)");
            }
        }
        return null;
    }

    /**
     * @return Direction or null if none found.
     * @throws NeepASM.ParseException if direction name is invalid
     */
    @Nullable
    private Direction parseDirection(TokenView view) throws NeepASM.ParseException
    {
        String name = view.nextIdentifier();
        if (!name.isEmpty())
        {
            Direction direction = directionByName(name);
            if (direction == null)
            {
                throw new NeepASM.ParseException("no such direction '" + name + "'\n Directions are specified with the full name or first letter (north or n)");
            }
            return direction;
        }
        return null;
    }

    /**
     * @param name Name of the direction (full name or first letter)
     * @return Direction or null if the name is invalid
     */
    @Nullable
    private Direction directionByName(String name)
    {
        name = name.toLowerCase();
        Direction d = Direction.byName(name);
        if (d == null)
        {
            d = switch (name)
            {
                case "n" -> Direction.NORTH;
                case "e" -> Direction.EAST;
                case "s" -> Direction.SOUTH;
                case "w" -> Direction.WEST;
                case "u" -> Direction.UP;
                case "d" -> Direction.DOWN;
                default -> null;
            };
        }
        return d;
    }

    @Nullable
    public KeyValue parseKV(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            String key = view.nextIdentifier();
            if (!key.isEmpty())
            {
                if (view.nextThing() == '=')
                {
                    String val = view.nextIdentifier();
                    if (!val.isEmpty())
                    {
                        entry.commit();
                        return new KeyValue(key, val);
                    }
                }
                throw new NeepASM.ParseException("malformed key-value pair");
            }
        }
        return null;
    }

    public void assureLineEnd(TokenView view) throws NeepASM.ParseException
    {
        view.fastForward();
        if (!isComment(view) && !view.lineEnded())
            throw new NeepASM.ParseException("unexpected token '" + view.nextBlob() + "'");
    }

    private boolean isDigit(char c)
    {
        return c == '-' || Character.isDigit(c);
    }

    public boolean isComment(TokenView view)
    {
        try (var ignored = view.save())
        {
            return view.nextThing() == '#';
        }
    }
}