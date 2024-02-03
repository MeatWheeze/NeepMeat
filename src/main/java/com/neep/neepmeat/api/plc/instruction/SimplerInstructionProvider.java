package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimplerInstructionProvider implements InstructionProvider
{
    private final SimpleInstructionProvider.NbtConstructor nbtConstructor;
    private final InstructionParser parser;
    private final Text shortName;

    public SimplerInstructionProvider(SimpleInstructionProvider.NbtConstructor nbtConstructor, InstructionParser parser, Text shortName)
    {
        this.nbtConstructor = nbtConstructor;
        this.parser = parser;
        this.shortName = shortName;
    }

    @Override
    public InstructionBuilder start(ServerWorld world, Consumer<Instruction> finished)
    {
        return null;
    }

    @Override
    public int maxArguments()
    {
        return 0;
    }

    @Override
    public Text getShortName()
    {
        return shortName;
    }

    @Override
    public String getParseName()
    {
        return shortName.getString();
    }

    @Override
    public Instruction createFromNbt(Supplier<World> world, NbtCompound nbt)
    {
        return nbtConstructor.create(world, nbt);
    }

    @Override
    public Instruction create(World world, List<Argument> arguments)
    {
        throw new NotImplementedException("Implement me!");
    }

    @Override
    public InstructionParser getParser()
    {
        return parser;
    }
}
