package com.neep.neepmeat.plc.opcode;

import com.neep.neepmeat.plc.program.PLCInstruction;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InstructionProvider
{
    protected final int arguments;
    protected final Constructor constructor;
    protected final Text shortName;

    public InstructionProvider(Constructor constructor, int arguments, Text shortName)
    {
        this.constructor = constructor;
        this.arguments = arguments;
        this.shortName = shortName;
    }

    public InstructionBuilder start(World world, Consumer<PLCInstruction> finished)
    {
        return new InstructionBuilder(this, world, finished);
    }

    int argumentCount()
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
        PLCInstruction create(Supplier<World> world, List<Argument> arguments);
    }

    public PLCInstruction create(World world, List<Argument> arguments)
    {
        return constructor.create(() -> world, arguments);
    }

    public record Argument(BlockPos pos, Direction face) { }
}
