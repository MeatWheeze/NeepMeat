package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.transport.block.item_transport.MergePipeBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemInPipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MergePipeBlockEntity extends PneumaticPipeBlockEntity
{
    public MergePipeBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MERGE_ITEM_PIPE, pos, state);
    }

    public MergePipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }


    @Override
    protected Direction getOutputDirection(ItemInPipe item, BlockState state, World world, Direction in)
    {
        return state.get(MergePipeBlock.FACING);
    }
}
