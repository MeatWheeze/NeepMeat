package com.neep.neepmeat.plc.instruction;

import net.minecraft.world.World;

import java.util.function.Supplier;

public interface ImmediateInstructionProvider extends InstructionProvider
{
    ImmediateInstruction createImmediate(Supplier<World> world);
}
