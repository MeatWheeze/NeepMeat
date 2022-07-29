package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public interface IMotorisedBlock
{
    void setConnectedMotor(@Nullable IMotorBlockEntity motor);

    IMotorBlockEntity getConnectedMotor();

    default boolean hasMotor()
    {
        return getConnectedMotor() != null;
    }

    default void setRunning(boolean running)
    {
        if (getConnectedMotor() != null)
        {
            getConnectedMotor().setRunning(running);
        }
    }

    default long doWork(long amount, TransactionContext transaction)
    {
        if (getConnectedMotor() != null)
            return getConnectedMotor().doWork(amount, transaction);
        else
            return -1;
    }

    default void update(ServerWorld world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(BaseFacingBlock.FACING);
        BlockPos backPos = pos.offset(facing.getOpposite());
        if (world.getBlockEntity(backPos) instanceof IMotorBlockEntity be
                && world.getBlockState(backPos).get(BaseFacingBlock.FACING) == facing)
        {
            setConnectedMotor(be);
        }
        else
        {
            setConnectedMotor(null);
        }
    }
}
