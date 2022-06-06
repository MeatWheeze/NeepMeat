package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MotorBlockEntity extends BloodMachineBlockEntity
{
    public MotorBlockEntity(BlockEntityType<MotorBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 1 * FluidConstants.BUCKET, 1 * FluidConstants.BUCKET);
    }

    public MotorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MOTOR, pos, state);
    }

}
