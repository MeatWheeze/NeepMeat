package com.neep.neepmeat.machine.motor;

import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class MotorBlockEntity extends BloodMachineBlockEntity implements MotorEntity
{
    public float rotorSpeed = 1f; // rad per tick
    public float currentSpeed = 0;
    public float angle;
    protected float influx;

    protected float loadTorque;

    protected BlockApiCache<Void, Void> cache = null;

    protected BloodAcceptor bloodAcceptor = new BloodAcceptor()
    {
        @Override
        public float getRate()
        {
            return 0;
        }

        @Override
        public void updateInflux(float influx)
        {
            MotorBlockEntity.this.influx = influx;
            onPowerChange();
        }

        @Override
        public Mode getMode()
        {
            return Mode.IN;
        }
    };

    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            motorised.tick(this);
        }
    }

    @Override
    protected void onPowerChange()
    {

        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            // TODO: Decide on float or double
            motorised.setInputPower((float) this.getMechPUPower());
        }
        super.onPowerChange();
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
        enabled = (!world.isReceivingRedstonePower(pos));
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
        return influx;
//        return getPUPower();
    }

    @Override
    public float getSpeed()
    {
        double P = PowerUtils.perUnitToAbsWatt(getMechPUPower());
        return  (float) (P / (loadTorque != 0 ? loadTorque : PowerUtils.MOTOR_TORQUE_LOSS));
    }

    @Override
    public long getMaxInsert()
    {
        return FluidConstants.BUCKET / 2;
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
