package com.neep.neepbus.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepbus.NeepBus;
import com.neep.neepbus.block.entity.RedstoneTransducerBlockEntity;
import com.neep.neepmeat.block.DataCableBlock;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneTransducerBlock extends DataCableBlock implements BlockEntityProvider, NeepBusProvider, DataCable
{
    public static final EnumProperty<Direction> FACING = Properties.FACING;

    public RedstoneTransducerBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings, settings);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state)
    {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockState superState = super.getPlacementState(ctx);
        if (superState == null || ctx.getPlayer() == null)
            return null;

        return this.getDefaultState().with(FACING, ctx.getPlayer().isSneaking() ? ctx.getPlayerLookDirection().getOpposite() : ctx.getPlayerLookDirection());
    }

    private int getRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        // Not sure why it has to be opposite. I would expect direction to be relative to this block, not the redstone wire.
        if (direction != state.get(FACING).getOpposite())
            return 0;

        if (world.getBlockEntity(pos) instanceof RedstoneTransducerBlockEntity be)
        {
            return be.getValue();
        }
        return 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return getRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        return getRedstonePower(state, world, pos, direction);
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.getBlockEntity(pos) instanceof RedstoneTransducerBlockEntity be)
        {
            updateRedstone(world, pos, be);
        }
    }

    public void updateRedstone(World world, BlockPos pos, RedstoneTransducerBlockEntity be)
    {
        // Avoid multiple block updates in the same tick
        if (be.lastRedstoneUpdate < world.getTime())
        {
            world.updateNeighbor(pos.offset(be.getCachedState().get(FACING)), this, pos);
            be.lastRedstoneUpdate = world.getTime();
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBus.REDSTONE_TRANSDUCER_BE.instantiate(pos, state);
    }
}
