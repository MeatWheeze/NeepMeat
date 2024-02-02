package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.program.Label;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParsedSource
{
    private final List<ParsedInstruction> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();
    private final List<ParsedMacro> macros = Lists.newArrayList();

    public void instruction(ParsedInstruction preInstruction)
    {
        instructions.add(preInstruction);
    }

    public void label(Label label)
    {
        labels.add(label);
    }

    public void macro(ParsedMacro macro)
    {
        macros.add(macro);
    }

    public int size()
    {
        return instructions.size();
    }

    public Iterable<ParsedInstruction> instructions()
    {
        return instructions;
    }

    public Iterable<ParsedMacro> macros()
    {
        return macros;
    }

    @Nullable
    public Label findLabel(String label)
    {
        return labels.stream().filter(l -> l.name().equals(label)).findFirst().orElse(null);
    }
}
