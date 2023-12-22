package com.neep.neepmeat.machine.crafting_station;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WorkstationBlock extends BaseBlock implements BlockEntityProvider
{
    public WorkstationBlock(String registryName, Settings settings)
    {
        super(registryName, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.WORKSTATION.instantiate(pos, state);
    }
}
