package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.pipe.AbstractAxialPipe;
import com.neep.neepmeat.block.pipe.IAxialPipe;
import com.neep.neepmeat.fluid_transfer.PipeState;
import com.neep.neepmeat.item.FluidComponentItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Function;

public class StopValveBlock extends AbstractAxialPipe implements PipeState.ISpecialPipe
{
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    public StopValveBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState().with(OPEN, false));
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


        return ActionResult.success(world.isClient);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN);
    }
}
