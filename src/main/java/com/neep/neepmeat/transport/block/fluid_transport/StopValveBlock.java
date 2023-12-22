package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.transport.api.pipe.AbstractAxialPipe;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StopValveBlock extends AbstractAxialPipe implements PipeState.ISpecialPipe, BlockEntityProvider
{
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty POWERED = Properties.POWERED;

    public StopValveBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(OPEN, true)
                .with(POWERED, false));
    }

    @Override
    public PipeState.FilterFunction getFlowFunction(World world, Direction bias, BlockPos pos, BlockState state)
    {
        return state.get(OPEN) ? PipeState::identity : PipeState::zero;
    }

    @Override
    public boolean canTransferFluid(Direction bias, BlockState state)
    {
        return state.get(OPEN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            world.setBlockState(pos, state.cycle(OPEN));
            updateNetwork((ServerWorld) world, pos, PipeNetwork.UpdateReason.VALVE_CHANGED);
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.isClient())
            return;

        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(POWERED))
        {
            if (state.get(OPEN) == powered)
            {
                state = state.with(OPEN, !powered);
            }
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);
            updateNetwork((ServerWorld) world, pos, PipeNetwork.UpdateReason.VALVE_CHANGED);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN, POWERED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
//        return new StopValveBlockEntity(pos, state);
        return null;
    }
}
