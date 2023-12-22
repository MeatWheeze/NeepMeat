package com.neep.neepmeat.block.entity.machine;

import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class AgitatorBlockEntity extends BlockEntity implements IMotorisedBlock
{
    public AgitatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public AgitatorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.AGITATOR, pos, state);
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        return false;
    }

    @Override
    public void setInputPower(float power)
    {

    }
}
