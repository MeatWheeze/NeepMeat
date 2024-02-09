package com.neep.neepmeat.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class MotorisedMachineBlockEntity extends SyncableBlockEntity implements MotorisedBlock, MotorisedBlock.DiagnosticsProvider
{
    protected float minPower;
    protected float power;
    protected float lastPower;
    protected float progressIncrement;
    protected float minIncrement;
    protected float maxIncrement;

    public MotorisedMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, float minPower, float minIncrement, float maxIncrement)
    {
        super(type, pos, state);
        this.minPower = minPower;
        this.minIncrement = minIncrement;
        this.maxIncrement = maxIncrement;
    }

    @Override
    public MotorisedBlock.Diagnostics getDiagnostics()
    {
        return MotorisedBlock.Diagnostics.insufficientPower(power < minPower, power, minPower);
    }

    public void serverTick()
    {
        if (lastPower != power)
        {
            lastPower = power;
            sync();
        }
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
        if (power < minPower)
        {
            progressIncrement = 0;
        }
        else
        {
            progressIncrement = MathHelper.lerp(power, minIncrement, maxIncrement);
        }
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("power", power);
        nbt.putFloat("increment", progressIncrement);
        nbt.putFloat("min_power", minPower);
        nbt.putFloat("min_increment", minIncrement);
        nbt.putFloat("max_increment", maxIncrement);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.power = nbt.getFloat("power");
        this.minPower = nbt.getFloat("min_power");
        this.progressIncrement = nbt.getFloat("increment");
        this.minIncrement = nbt.getFloat("min_increment");
        this.maxIncrement = nbt.getFloat("max_increment");
    }

    public float progressIncrement()
    {
        return progressIncrement;
    }

    public float minIncrement()
    {
        return minIncrement;
    }

    public float maxIncrement()
    {
        return maxIncrement;
    }
}