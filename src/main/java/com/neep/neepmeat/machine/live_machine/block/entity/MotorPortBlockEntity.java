package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MotorPortBlockEntity extends SyncableBlockEntity implements MotorisedBlock, LivingMachineComponent
{
    private float inputPower;

    public MotorPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public float getPower()
    {
        return inputPower;
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        return false;
    }

    @Override
    public void setInputPower(float power)
    {
        inputPower = power;
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
    public ComponentType<?> getComponentType()
    {
        return LivingMachineComponents.MOTOR_PORT;
    }
}
