package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.blockentity.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends BloodMachineBlockEntity implements IMotorBlockEntity
{
    public boolean running;
    public boolean starting;
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;

    protected IMotorisedBlock cache = null;

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, FluidConstants.BUCKET, FluidConstants.BUCKET);
        this.maxRunningRate = FluidConstants.BUCKET / 2;
    }

    public MotorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MOTOR, pos, state);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (cache == null)
        {
            update(world, pos, pos, getCachedState());
        }

        if (cache != null)
        {
            float mult = (this.runningRate / (float) this.maxRunningRate);
            cache.setWorkMultiplier(mult);
            cache.tick(this);
        }
    }

    @Override
    public void setConnectedBlock(IMotorisedBlock motorised)
    {
        this.cache = motorised;
    }

    @Override
    public float getRotorAngle()
    {
        return angle;
    }

    @Override
    public IMotorisedBlock getConnectedBlock()
    {
        return cache;
    }
}
