package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractAxialPipe;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.machine.fluid.BlockPipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CheckValveBlock extends AbstractAxialPipe implements BlockEntityProvider
{
    public CheckValveBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CHECK_VALVE.instantiate(pos, state);
    }

    public static class CheckValvePipeVertex extends BlockPipeVertex
    {
        public CheckValvePipeVertex(FluidPipeBlockEntity fluidPipeBlockEntity)
        {
            super(fluidPipeBlockEntity);
        }

        @Override
        public long canInsert(ServerWorld world, int inDir, FluidVariant variant, long maxAmount)
        {
            long superAmount = super.canInsert(world, inDir, variant, maxAmount);
            return  inDir == parent.getCachedState().get(FACING).ordinal() ? superAmount : 0;
        }

        @Override
        public boolean canSimplify()
        {
            return false;
        }
    }
}