package com.neep.neepmeat.machine.solidity_detector;

import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SolidityDetectorBlockEntity extends BlockEntity
{
    public SolidityDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public SolidityDetectorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.SOLIDITY_DETECTOR, pos, state);
    }

    public boolean test(Direction facing, BlockPos offset)
    {
        BlockState state = world.getBlockState(offset);
        if (state.isSideSolid(world, pos, facing, SideShapeType.FULL))
        {
            return true;
        }
        return false;
    }
}
