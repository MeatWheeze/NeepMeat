package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.PreInstruction;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Parser
{
    private final Map<String, InstructionProvider> instructionMap = Maps.newHashMap();

    private ParsedSource parsedSource;

    public Parser()
    {
        Instructions.REGISTRY.forEach(provider ->
        {
            instructionMap.put(provider.getParseName().toLowerCase(), provider);
        });
    }

    @Nullable
    public ParsedSource parse(String source)
    {
        parsedSource = new ParsedSource();

        int line1 = 0;
        for (var line : source.split("\n"))
        {
            TokenView view = new TokenView(line);

            try
            {
                parseLine(view);
            }
            catch (Exception e)
            {
                System.out.println("Error at " + line1 + ":" + view.pos() + ": " + e.getMessage());
//                e.printStackTrace();
                return null;
            }
            line1++;
        }
        return parsedSource;
    }

    private void parseLine(TokenView view) throws NeepASM.ParseException
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
                if (!view.lineEnded() && !isComment(view))
                {
                    throw new NeepASM.ParseException("unexpected token after '" + token + "'");
                }

                entry.commit();
                return;
            }
        }

        parseInstruction(view);
    }

    private void parseInstruction(TokenView view) throws NeepASM.ParseException
    {
        view.fastForward();
        if (view.lineEnded())
            return;

        List<Argument> arguments = Lists.newArrayList();
        List<KeyValue> kvs = Lists.newArrayList();

        String id = view.nextIdentifier();
        InstructionProvider provider = readInstruction(id);
        if (provider != null)
        {
            boolean readLine = true;
            while (readLine)
            {
                Argument a;
                KeyValue kv;
                if (isComment(view) || view.lineEnded())
                {
                    readLine = false;
                }
                else if ((a = parseArgument(view)) != null)
                {
                    arguments.add(a);
                }
                else if ((kv = parseKV(view)) != null)
                {
                    kvs.add(kv);
                }
                else
                {
                    throw new NeepASM.ParseException("unexpected token", view.peek());
                }
                view.fastForward();
            }

            parsedSource.instruction(new PreInstruction(provider, arguments, kvs));
        }
        else
        {
            throw new NeepASM.ParseException(id, "unrecognised operation '" + id + "'");
        }
    }

    @Nullable
    private InstructionProvider readInstruction(String id)
    {
        return instructionMap.get(id.toLowerCase());
    }

    @Nullable
    private Argument parseArgument(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            if (view.nextThing() == '@')
            {
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
                        entry.commit();
                        return new Argument(new BlockPos(x, y, z), direction);
                    }
                }
                throw new NeepASM.ParseException("malformed argument\n Arguments should be of the form '@(<x> <y> <z> <direction>)");
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
    private KeyValue parseKV(TokenView view) throws NeepASM.ParseException
    {
        try (var entry = view.save())
        {
            String key = view.nextIdentifier();
            if (!key.isEmpty())
            {
                if (view.nextThing() == '=')
                {
                    String val = view.nextIdentifier();
                    entry.commit();
                    return new KeyValue(key, val);
                }
                else
                {
                    throw new NeepASM.ParseException("malformed key-value pair");
                }
            }
        }
        return null;
    }

    private boolean isDigit(char c)
    {
        return c == '-' || Character.isDigit(c);
    }

    private boolean isComment(TokenView view)
    {
        try (var ignored = view.save())
        {
            return view.nextThing() == '#';
        }
    }
}
