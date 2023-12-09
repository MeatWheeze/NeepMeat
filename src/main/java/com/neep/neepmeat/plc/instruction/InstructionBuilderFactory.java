package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionBuilder;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import net.minecraft.world.World;

import java.util.function.Consumer;

@FunctionalInterface
public interface InstructionBuilderFactory
{
    InstructionBuilder create(InstructionProvider provider, World world, Consumer<Instruction> finished);
}
