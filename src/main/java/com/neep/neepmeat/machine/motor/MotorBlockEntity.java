package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends BloodMachineBlockEntity implements IMotorBlockEntity
{
    public boolean starting;
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;

    protected BlockApiCache<Void, Void> cache = null;

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
            update((ServerWorld) world, pos, pos, getCachedState());
        }
    }

    @Override
    protected void onRateChange()
    {
        super.onRateChange();
        if (cache != null && cache.getBlockEntity() instanceof IMotorisedBlock motorised)
        {
            motorised.setWorkMultiplier(getRunningRate());
            motorised.tick(this);
        }
    }

    @Override
    public void setConnectedBlock(BlockApiCache<Void, Void> motorised)
    {
        this.cache = motorised;
    }

    @Override
    public float getRotorAngle()
    {
        return angle;
    }

    @Override
    public void update(ServerWorld world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        IMotorBlockEntity.super.update(world, pos, fromPos, state);
        enabled = (!world.isReceivingRedstonePower(pos));
    }

    @Override
    public BlockApiCache<Void, Void> getConnectedBlock()
    {
        return cache;
    }

    public static float rateToSpeed(float rate)
    {
        return rate * 20;
    }

    @Override
    public float getSpeed()
    {
        return MotorBlockEntity.rateToSpeed(getRunningRate());
    }
}
