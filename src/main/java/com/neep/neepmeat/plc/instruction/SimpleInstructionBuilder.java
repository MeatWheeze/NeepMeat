package com.neep.neepmeat.plc.instruction;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionBuilder;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class SimpleInstructionBuilder implements InstructionBuilder
{
    private final InstructionProvider provider;
    private final List<Argument> arguments = Lists.newArrayList();
    private final World world;
    private final Consumer<Instruction> finished;

    public SimpleInstructionBuilder(InstructionProvider provider, World world, Consumer<Instruction> finished)
    {
        this.provider = provider;
        this.world = world;
        this.finished = finished;

        if (isComplete())
        {
            finished.accept(build());
        }
    }

    public InstructionBuilder argument(Argument argument)
    {
        arguments.add(argument);
        if (isComplete())
        {
            finished.accept(build());
            arguments.clear();
        }
        return this;
    }

    public boolean isComplete()
    {
        return arguments.size() >= provider.argumentCount();
    }

    public Instruction build()
    {
        return provider.create(world, arguments);
    }
}
