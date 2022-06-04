package com.neep.neepmeat.block.fluid_transport;

import com.neep.neepmeat.block.pipe.AbstractAxialPipe;
import com.neep.neepmeat.blockentity.CheckValveBlockEntity;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeState;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.item.FluidComponentItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Function;

public class CheckValveBlock extends AbstractAxialPipe implements IVariableFlowBlock, PipeState.ISpecialPipe
{
    public CheckValveBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient())
        {
            Direction facing = state.get(FACING);
            System.out.println(FluidNetwork.getInstance(world).getNodeSupplier(new NodePos(pos.offset(facing), facing.getOpposite())).get());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Direction facing = state.get(FACING);
        if (direction == facing)
        {
            return AcceptorModes.PUSH;
        }
        else if (direction == facing.getOpposite())
        {
            return AcceptorModes.INSERT_ONLY;
        };
        return AcceptorModes.NONE;
    }

    @Override
    public float getFlow(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof CheckValveBlockEntity be)
        {
            return be.getApparentFlow();
        }
        return 0;
    }

    @Override
    public Function<Long, Long> getFlowFunction(Direction bias, BlockState state)
    {
        if (bias == state.get(FACING))
            return Function.identity();
        else
            return flow -> 0L;
    }

    @Override
    public boolean canTransferFluid(Direction bias, BlockState state)
    {
        return bias == state.get(FACING);
    }
}