package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.compiler.alias.ParsedAlias;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.program.Label;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.neep.neepmeat.neepasm.program.Label.Seek.FORWARDS;

public class ParsedSource
{
    private final List<ObjectIntPair<ParsedInstruction>> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();
    private final List<ParsedFunction> functions = Lists.newArrayList();
    private final List<ParsedAlias> aliases = Lists.newArrayList();

    public void instruction(ParsedInstruction preInstruction, int line)
    {
        instructions.add(ObjectIntPair.of(preInstruction, line));
    }

    public void label(Label label)
    {
        labels.add(label);
    }

    public void function(ParsedFunction function)
    {
        functions.add(function);
    }

    public void alias(ParsedAlias alias)
    {
        aliases.add(alias);
    }

    public int size()
    {
        return instructions.size();
    }

    public Iterable<ObjectIntPair<ParsedInstruction>> instructions()
    {
        return instructions;
    }

    public Iterable<ParsedFunction> functions()
    {
        return functions;
    }

    @Nullable
    public Label findLabel(String label)
    {
        return labels.stream().filter(l -> l.name().equals(label)).findFirst().orElse(null);
    }

    @Nullable
    public Label findLabel(String label, int origin, Label.Seek seek)
    {
        return switch (seek)
        {
            case FORWARDS -> labels.stream().filter(l -> l.name().equals(label) && l.index() >= origin).findFirst().orElse(null);
            case BACKWARDS -> labels.stream().filter(l -> l.name().equals(label) && l.index() <= origin).findFirst().orElse(null);
            case ABSOLUTE -> findLabel(label);
        };
    }

    @Nullable
    public ParsedFunction findFunction(String name)
    {
        return functions.stream().filter(m -> m.name().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public ParsedAlias findAlias(String name)
    {
        return aliases.stream().filter(a -> a.name().equals(name)).findFirst().orElse(null);
    }
}
