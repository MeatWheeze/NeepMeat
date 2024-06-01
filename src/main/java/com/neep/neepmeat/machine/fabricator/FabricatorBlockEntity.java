package com.neep.neepmeat.machine.fabricator;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class FabricatorBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    public FabricatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
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
