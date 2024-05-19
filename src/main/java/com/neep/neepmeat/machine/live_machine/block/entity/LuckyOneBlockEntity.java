package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.transport.api.pipe.AbstractBloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LuckyOneBlockEntity extends SyncableBlockEntity implements LivingMachineComponent
{
    private final AbstractBloodAcceptor acceptor = new AbstractBloodAcceptor()
    {
        @Override
        public Mode getMode()
        {
            return Mode.SINK;
        }

        @Override
        public float updateInflux(float influx)
        {
            if (influx >= 0.05)
            {
                LuckyOneBlockEntity.this.influx = 0.05f;
                sync();
                return 0.05f;
            }
            else
            {
                LuckyOneBlockEntity.this.influx = 0;
                sync();
                return 0;
            }
        }
    };

    public int animationOffset = -1;
    private float influx;

    public LuckyOneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (animationOffset == -1)
        {
            animationOffset = world.getRandom().nextInt(200);
            markDirty();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("influx", influx);
        nbt.putInt("start_time", animationOffset);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.influx = nbt.getFloat("influx");
        this.animationOffset = nbt.getInt("start_time");
    }

    public float getInflux()
    {
        return influx;
    }

    public BloodAcceptor getAcceptor(Direction unused)
    {
        return acceptor;
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.LUCKY_ONE;
    }

    public boolean isActive()
    {
        return influx >= 0.05;
    }
}
