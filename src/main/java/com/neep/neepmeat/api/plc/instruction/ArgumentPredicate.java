package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.plc.instruction.Argument;
import net.minecraft.world.World;

@FunctionalInterface
public interface ArgumentPredicate
{
    boolean test(World world, Argument argument) throws InstructionException;
}
