package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.instruction.InstructionBuilder;
import net.minecraft.world.World;

import java.util.function.Consumer;

@FunctionalInterface
public interface InstructionBuilderFactory
{
    InstructionBuilder create(InstructionProvider provider, World world, Consumer<Instruction> finished);
}
