package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IMotorBlockEntity
{
    default void update(World world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(BaseFacingBlock.FACING);
        if ((world.getBlockEntity(pos.offset(facing)) instanceof IMotorisedBlock block))
        {
            setConnectedBlock(block);
        }
    }

    void setConnectedBlock(IMotorisedBlock motorised);

    float getRotorAngle();

    IMotorisedBlock getConnectedBlock();
}
