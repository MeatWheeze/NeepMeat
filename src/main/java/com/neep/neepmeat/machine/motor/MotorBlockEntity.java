package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.PowerUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends LiquidFuelMachine implements MotorEntity
{
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;
    protected float outputPower = 0;
    protected float loadTorque;


    protected BlockApiCache<Void, Void> cache = null;

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void tick()
    {
        if (cache == null)
        {
            update((ServerWorld) world, pos, pos, getCachedState());
        }
        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            motorised.tick(this);
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            long energy = 100;
            long extracted = extractEnergy(energy, transaction);

            float newPower;
            if (extracted > 0)
            {
                newPower = (float) PowerUtils.absoluteToPerUnit(extracted);
            }
            else newPower = 0;

            if (newPower != outputPower)
            {
                outputPower = newPower;
                onPowerChange();
            }

            transaction.commit();
        }
    }

    protected void onPowerChange()
    {
        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            // TODO: Decide on float or double
            motorised.setInputPower((float) this.getMechPUPower());
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
        MotorEntity.super.update(world, pos, fromPos, state);
//        enabled = (!world.isReceivingRedstonePower(pos));
        loadTorque = updateLoadTorque();
        sync();
    }

    @Override
    public BlockApiCache<Void, Void> getConnectedBlock()
    {
        return cache;
    }

    @Override
    public double getMechPUPower()
    {
        return outputPower;
    }

    @Override
    public float getSpeed()
    {
        double P = PowerUtils.perUnitToAbsWatt(getMechPUPower());
        return  (float) (P / (loadTorque != 0 ? loadTorque : PowerUtils.MOTOR_TORQUE_LOSS));
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("loadTorque", loadTorque);
        nbt.putFloat("influx", outputPower);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.loadTorque = nbt.getFloat("loadTorque");
        this.outputPower = nbt.getFloat("influx");
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
    }

}
