package com.neep.neepmeat.machine.motor;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public interface IMotorBlockEntity
{
    long doWork(long amount, TransactionContext transaction);

    void setRunning(boolean running);

    void update(World world, BlockPos pos, BlockPos fromPos, BlockState state);
}
