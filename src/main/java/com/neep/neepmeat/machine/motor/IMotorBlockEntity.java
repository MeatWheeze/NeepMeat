package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.IMotorisedBlock;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public interface IMotorBlockEntity
{
    default long doWork(long amount, TransactionContext transaction) {return 0;}

    default void setRunning(boolean running) {}

    default void update(World world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(BaseFacingBlock.FACING);
        if ((world.getBlockEntity(pos.offset(facing)) instanceof IMotorisedBlock block))
        {
            setConnectedBlock(block);
        }
    }

    void setConnectedBlock(IMotorisedBlock motorised);

    IMotorisedBlock getConnectedBlock();
}
