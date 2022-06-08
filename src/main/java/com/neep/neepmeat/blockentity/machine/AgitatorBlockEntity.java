package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AgitatorBlockEntity extends BlockEntity implements IMotorisedBlock
{
    protected MotorBlockEntity motor;

    public AgitatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public AgitatorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.AGITATOR, pos, state);
    }

    @Override
    public void setConnectedMotor(@Nullable MotorBlockEntity motor)
    {
        this.motor = motor;
    }

    @Override
    public MotorBlockEntity getConnectedMotor()
    {
        return motor;
    }
}
