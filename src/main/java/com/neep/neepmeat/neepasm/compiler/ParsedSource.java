package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.PreInstruction;
import com.neep.neepmeat.neepasm.program.Label;

import java.util.List;

public class ParsedSource
{
    private final List<PreInstruction> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();

    public void instruction(PreInstruction preInstruction)
    {
        instructions.add(preInstruction);
    }

    public void label(Label label)
    {
        labels.add(label);
    }

    public int size()
    {
        return instructions.size();
    }

    public Iterable<PreInstruction> instructions()
    {
        return instructions;
    }
}
