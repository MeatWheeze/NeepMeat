package com.neep.neepmeat.machine.reactor;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * To be implemented by blocks.
 * Allows ReceiverOrganismComponent lookup without getting the block entity every time (unlike BlockApiLookup)
 */
public interface ReceiverOrganismComponentProvider
{
    ReceiverOrganismComponent get(World world, BlockPos pos, BlockState state);
}
