package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.transport.api.pipe.AbstractAxialPipe;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.PipeState;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.item.FluidComponentItem;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CheckValveBlock extends AbstractAxialPipe implements BlockEntityProvider, PipeState.ISpecialPipe
{
    public CheckValveBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        if (!world.isClient())
//        {
//            Direction facing = state.get(FACING);
//            System.out.println(FluidNodeManager.getInstance(world).getNodeSupplier(new NodePos(pos, facing)).get());
//        }
        return super.onUse(state, world, pos, player, hand, hit);
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
    public PipeState.FilterFunction getFlowFunction(World world, Direction bias, BlockPos pos, BlockState state)
    {
        if (bias == state.get(FACING))
            return PipeState::identity;
        else
            return (variant, flow) -> 0L;

    }

    @Override
    public boolean canTransferFluid(Direction bias, BlockState state)
    {
        return bias == state.get(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
    }
}