package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.instruction.InstructionBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface InstructionProvider
{
    InstructionBuilder start(ServerWorld world, Consumer<Instruction> finished);

    int maxArguments();

    Text getShortName();
    String getParseName();

    Instruction createFromNbt(Supplier<World> world, NbtCompound nbt);

    Instruction create(World world, List<Argument> arguments);

}
