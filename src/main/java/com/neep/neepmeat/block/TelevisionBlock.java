package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TelevisionBlock extends BaseBlock
{
    public static final IntProperty ROTATION = Properties.ROTATION;

    public TelevisionBlock(String name, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(name, itemSettings, settings);
        setDefaultState(getDefaultState().with(ROTATION, 0));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(ROTATION, MathHelper.floor((ctx.getPlayerYaw() * 16.0f / 360.0f) + 0.5) & 0xF);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        world.setBlockState(pos, getState(world, pos, state));
    }

    private BlockState getState(World world, BlockPos pos, BlockState state)
    {
        if (world.getReceivedRedstonePower(pos) > 0)
        {
            return NMBlocks.TELEVISION_STATIC.getDefaultState().with(ROTATION, state.get(ROTATION));
        }
        else
        {
            return NMBlocks.TELEVISION_OFF.getDefaultState().with(ROTATION, state.get(ROTATION));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ROTATION);
    }
}
