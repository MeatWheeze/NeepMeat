package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

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
    public void tick(IMotorBlockEntity motor)
    {

    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {

    }
}
