package com.neep.neepmeat.api.live_machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class LivingMachineBlock extends Block implements BlockEntityProvider
{
    public LivingMachineBlock(Settings settings)
    {
        super(settings);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return !world.isClient() ? (world1, pos, state1, blockEntity) ->
        {
            if (blockEntity instanceof LivingMachineBlockEntity be)
            {
                be.serverTick();
            }
        } : null;
    }
//    protected abstract <T extends LivingMachineBlockEntity> T makeBlockEntity(BlockPos pos, BlockState state);
}
