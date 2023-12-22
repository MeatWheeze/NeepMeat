package com.neep.neepmeat.block;

import com.neep.neepmeat.block.pipe.AbstractAxialPipe;
import com.neep.neepmeat.blockentity.StopValveBlockEntity;
import com.neep.neepmeat.fluid_transfer.PipeState;
import com.neep.neepmeat.item.FluidComponentItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class StopValveBlock extends AbstractAxialPipe implements PipeState.ISpecialPipe, BlockEntityProvider
{
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    public StopValveBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState().with(OPEN, true));
    }

    @Override
    public Function<Long, Long> getFlowFunction(Direction bias, BlockState state)
    {
        return state.get(OPEN) ? Function.identity() : PipeState::zero;
    }

    @Override
    public boolean canTransferFluid(Direction bias, BlockState state)
    {
        return state.get(OPEN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        world.setBlockState(pos, state.cycle(OPEN));

        return ActionResult.success(world.isClient);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new StopValveBlockEntity(pos, state);
    }
}
