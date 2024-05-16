package com.neep.neepmeat.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SpecialRail
{
    void apply(World world, BlockPos pos, BlockState state, AbstractMinecartEntity minecart);
}
