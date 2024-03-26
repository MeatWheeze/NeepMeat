package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.PowerUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends LiquidFuelMachine implements MotorEntity
{
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;
    protected float outputPower = 0;
    protected float loadTorque;

    @Nullable protected BlockApiCache<MotorisedBlock, Void> cache = null;
    @Nullable private MotorisedBlock lastMotorised;

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void tick()
    {
        if (cache == null)
        {
            Direction facing = getCachedState().get(BaseFacingBlock.FACING);
            cache = BlockApiCache.create(MotorisedBlock.LOOKUP, (ServerWorld) world, pos.offset(facing));
        }

        MotorisedBlock motorised = cache.find(null);
        if (motorised != null)
        {
            if (motorised.getLoadTorque() != loadTorque)
            {
                loadTorque = motorised.getLoadTorque();
                sync();
            }

            if (motorised != lastMotorised)
            {
                lastMotorised = motorised;
                onPowerChange();
            }

            motorised.motorTick(this);
        }
        else
        {
            lastMotorised = null;
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            long extracted = extractEnergy(maxPower(), transaction);

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
        MotorisedBlock motorised = getConnectedBlock();
        if (motorised != null)
        {
            // TODO: Decide on float or double
            motorised.setInputPower((float) this.getMechPUPower());
        }
        sync();
    }

    @Override
    public float getRotorAngle()
    {
        return angle;
    }

    @Override
    public double getMechPUPower()
    {
        return outputPower;
    }

    @Override
    public MotorisedBlock getConnectedBlock()
    {
        return cache != null ? cache.find(null) : null;
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
}
