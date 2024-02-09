package com.neep.neepmeat.block.entity.machine;

import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class AgitatorBlockEntity extends BlockEntity implements MotorisedBlock
{
    public AgitatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        return false;
    }

    @Override
    public void setInputPower(float power)
    {

    }
}
