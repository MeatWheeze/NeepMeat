package com.neep.neepmeat.api.plc.instruction;

import net.minecraft.world.World;

@FunctionalInterface
public interface ArgumentPredicate
{
    boolean test(World world, Argument argument) throws InstructionException;
}
