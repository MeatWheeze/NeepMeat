package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends BloodMachineBlockEntity implements IMotorBlockEntity
{
    public boolean starting;
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;

    protected IMotorisedBlock cache = null;

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
            float mult = getRunningRate();
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
    public void update(World world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        IMotorBlockEntity.super.update(world, pos, fromPos, state);
        enabled = (!world.isReceivingRedstonePower(pos));
    }

    @Override
    public IMotorisedBlock getConnectedBlock()
    {
        return cache;
    }

    public static float rateToSpeed(float rate)
    {
        return rate * 20;
    }
}
