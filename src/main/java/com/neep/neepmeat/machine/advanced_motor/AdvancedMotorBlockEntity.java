package com.neep.neepmeat.machine.advanced_motor;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AdvancedMotorBlockEntity extends SyncableBlockEntity implements MotorEntity
{
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;
    protected float influx;

    protected float loadTorque;
    @Nullable protected MotorisedBlock lastMotorised;

    @Nullable protected BlockApiCache<Void, Void> cache = null;

    protected BloodAcceptor bloodAcceptor = new BloodAcceptor()
    {
        @Override
        public long getOutput()
        {
            return 0;
        }

        @Override
        public void updateInflux(float influx)
        {
            AdvancedMotorBlockEntity.this.influx = influx;
            onPowerChange();
        }

        @Override
        public Mode getMode()
        {
            return Mode.IN;
        }
    };

    public AdvancedMotorBlockEntity(BlockEntityType<AdvancedMotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        if (cache == null)
        {
            Direction facing = getCachedState().get(BaseFacingBlock.FACING);
            cache = BlockApiCache.create(MeatLib.VOID_LOOKUP, (ServerWorld) world, pos.offset(facing));
        }

        if (cache.getBlockEntity() instanceof MotorisedBlock motorised)
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

            motorised.tick(this);
        }
    }

    protected void onPowerChange()
    {
        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            // TODO: Decide on float or double
            motorised.setInputPower((float) this.getMechPUPower());
        }
        sync();
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
    public BlockApiCache<Void, Void> getConnectedBlock()
    {
        return cache;
    }

    @Override
    public double getMechPUPower()
    {
        return influx;
    }

    @Override
    public float getSpeed()
    {
        double power = PowerUtils.perUnitToAbsWatt(getMechPUPower());
        return (float) (power / (loadTorque != 0 ? loadTorque : PowerUtils.MOTOR_TORQUE_LOSS));
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("loadTorque", loadTorque);
        nbt.putFloat("influx", influx);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.loadTorque = nbt.getFloat("loadTorque");
        this.influx = nbt.getFloat("influx");
    }

    public BloodAcceptor getBloodAcceptor(Direction face)
    {
        return bloodAcceptor;
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
    }
}
