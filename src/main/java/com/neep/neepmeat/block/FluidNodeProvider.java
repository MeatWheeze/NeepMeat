package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.FluidNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface FluidNodeProvider extends DirectionalFluidAcceptor
{
//    boolean connectInDirection(World world, BlockState state, Direction direction);

    FluidNode getNode(World world, BlockPos pos, Direction direction);

}
