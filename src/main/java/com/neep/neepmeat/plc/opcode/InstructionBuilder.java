package com.neep.neepmeat.plc.opcode;

import com.google.common.collect.Lists;
import com.neep.neepmeat.plc.program.PLCInstruction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class InstructionBuilder
{
    private final InstructionProvider provider;
    private final List<InstructionProvider.Argument> arguments = Lists.newArrayList();
    private final World world;
    private final Consumer<PLCInstruction> finished;

    public InstructionBuilder(InstructionProvider provider, World world, Consumer<PLCInstruction> finished)
    {
        this.provider = provider;
        this.world = world;
        this.finished = finished;

        if (isComplete())
        {
            finished.accept(build());
        }
    }

    public InstructionBuilder argument(BlockPos pos, Direction face)
    {
        arguments.add(new InstructionProvider.Argument(pos, face));
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

    public PLCInstruction build()
    {
        return provider.create(world, arguments);
    }
}
