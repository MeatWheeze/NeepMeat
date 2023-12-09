package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.plc.instruction.InstructionBuilderFactory;
import com.neep.neepmeat.plc.instruction.SimpleInstructionBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleInstructionProvider implements InstructionProvider
{
    protected final int arguments;
    protected final Constructor constructor;
    protected final NbtConstructor nbtConstructor;
    protected final Text shortName;
    protected InstructionBuilderFactory factory = SimpleInstructionBuilder::new;

    public SimpleInstructionProvider(Constructor constructor, NbtConstructor nbtConstructor, int arguments, Text shortName)
    {
        this.constructor = constructor;
        this.nbtConstructor = nbtConstructor;
        this.arguments = arguments;
        this.shortName = shortName;
    }

    public SimpleInstructionProvider factory(InstructionBuilderFactory factory)
    {
        this.factory = factory;
        return this;
    }

    public InstructionBuilder start(ServerWorld world, Consumer<Instruction> finished)
    {
        return factory.create(this, world, finished);
    }

    public int argumentCount()
    {
        return arguments;
    }

    public Text getShortName()
    {
        return shortName;
    }


    @FunctionalInterface
    public interface Constructor
    {
        Instruction create(Supplier<World> world, List<Argument> arguments);
    }

    @FunctionalInterface
    public interface NbtConstructor
    {
        Instruction create(Supplier<World> world, NbtCompound nbt);
    }

    public Instruction create(World world, List<Argument> arguments)
    {
        return constructor.create(() -> world, arguments);
    }

    public Instruction createFromNbt(Supplier<World> world, NbtCompound nbt)
    {
        return nbtConstructor.create(world, nbt);
    }
}
