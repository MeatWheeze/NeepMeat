package com.neep.neepmeat.block.machine;

import com.neep.neepmeat.blockentity.machine.MotorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public interface IKineticBlock
{
    void setConnectedMotor(@Nullable MotorBlockEntity motor);

    MotorBlockEntity getConnectedMotor();

    default boolean hasMotor()
    {
        return getConnectedMotor() != null;
    }

    default long doWork(long amount, TransactionContext transaction)
    {
        return getConnectedMotor().doWork(amount, transaction);
    }

    default void update(ServerWorld world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(LinearOscillatorBlock.FACING);
        BlockPos backPos = pos.offset(facing.getOpposite());
        if (world.getBlockEntity(backPos) instanceof MotorBlockEntity be)
        {
            setConnectedMotor(be);
        }
        else
        {
            setConnectedMotor(null);
        }
    }
}
