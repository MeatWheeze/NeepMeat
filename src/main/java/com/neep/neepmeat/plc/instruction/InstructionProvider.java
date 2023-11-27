package com.neep.neepmeat.plc.instruction;

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

    int argumentCount();

    Text getShortName();

    Instruction createFromNbt(Supplier<World> world, NbtCompound nbt);

    Instruction create(ServerWorld world, List<Argument> arguments);

    interface Immediate extends InstructionProvider
    {
        ImmediateInstruction createImmediate(Supplier<World> world);
    }
}
