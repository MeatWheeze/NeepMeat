package com.neep.neepmeat.plc.instruction;

import com.google.common.collect.Lists;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.function.Consumer;

public class InstructionBuilder
{
    private final InstructionProvider provider;
    private final List<Argument> arguments = Lists.newArrayList();
    private final ServerWorld world;
    private final Consumer<Instruction> finished;

    public InstructionBuilder(InstructionProvider provider, ServerWorld world, Consumer<Instruction> finished)
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
