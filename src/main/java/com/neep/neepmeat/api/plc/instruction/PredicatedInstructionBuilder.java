package com.neep.neepmeat.api.plc.instruction;

import com.google.common.collect.Lists;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionBuilderFactory;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class PredicatedInstructionBuilder implements InstructionBuilder
{
    private final List<Argument> arguments = Lists.newArrayList();
    private final InstructionProvider provider;
    private final World world;
    private final Consumer<Instruction> finished;
    private final List<ArgumentPredicate> predicates;

    PredicatedInstructionBuilder(InstructionProvider provider, World world, Consumer<Instruction> finished, List<ArgumentPredicate> predicates)
    {
        this.provider = provider;
        this.world = world;
        this.finished = finished;
        this.predicates = predicates;
    }

    @Override
    public InstructionBuilder argument(Argument argument) throws InstructionException
    {
        predicates.get(arguments.size()).test(world, argument);

        arguments.add(argument);

        if (isComplete())
        {
            finished.accept(build());
            arguments.clear();
        }
        return this;
    }

    @Override
    public InstructionBuilder keyValue(KeyValue kv) throws InstructionException
    {
        return this;
    }

    @Override
    public boolean isComplete()
    {
        return arguments.size() == provider.maxArguments();
    }

    @Override
    public Instruction build()
    {
        return provider.create(world, arguments);
    }

    @Override
    public int argumentCount()
    {
        return arguments.size();
    }

    public static Builder create()
    {
        return new Builder();
    }

    // It's an instruction builder builder. InstructionProvider is an instruction builder builder builder.
    public static class Builder implements InstructionBuilderFactory
    {
        private final List<ArgumentPredicate> predicates = Lists.newArrayList();

        public Builder arg(ArgumentPredicate predicate)
        {
            predicates.add(predicate);
            return this;
        }

        @Override
        public InstructionBuilder create(InstructionProvider provider, World world, Consumer<Instruction> finished)
        {
            if (provider.maxArguments() != predicates.size())
                throw new IllegalStateException(provider.getShortName() + ": Argument and predicate count do not match. Fix your code!");

            return new PredicatedInstructionBuilder(provider, world, finished, predicates);
        }
    }
}
