package com.neep.neepmeat.plc.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PLCRedstoneInterface extends BaseBlock implements BlockEntityProvider, DataCable
{
    public PLCRedstoneInterface(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return PLCBlocks.REDSTONE_INTERFACE_ENTITY.instantiate(pos, state);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state)
    {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        if (world.getBlockEntity(pos) instanceof PLCRedstoneInterfaceBlockEntity be)
        {
            return be.getReceivedStrength();
        }
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof PLCRedstoneInterfaceBlockEntity be)
        {
            be.updateReceived(world.getReceivedRedstonePower(pos));
        }
    }
}
