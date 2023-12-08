package com.neep.neepmeat.plc.arm;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.plc.PLCBlocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RoboticArmBlock extends BaseBlock implements BlockEntityProvider
{
    public RoboticArmBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return PLCBlocks.ROBOTIC_ARM_ENTITY.instantiate(pos, state);
    }
}
