package com.neep.neepmeat.transport.block.energy_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VascularConduitBlock extends AbstractPipeBlock implements BlockEntityProvider, VascularConduit
{
    public VascularConduitBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction direction, World world, BlockPos pos)
    {
//        var other = VascularConduitEntity.LOOKUP.find(world, pos, null);
//        var other = VascularConduit.find(world, pos, state);
//        return other != null;
        if (state.getBlock() instanceof VascularConduit)
        {
            return true;
        }
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        boolean forced = type == PipeConnectionType.FORCED;
        boolean otherConnected = false;

        boolean canConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);

        // Check if neighbour is forced
        if (neighborState.isOf(this))
        {
            forced = forced || neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.FORCED;
            otherConnected = neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.SIDE;

        }

        // AAAAAAAAAAAAAAAAAAAA
        PipeConnectionType finalConnection =
                otherConnected ? PipeConnectionType.SIDE :
                        forced ? PipeConnectionType.FORCED
                                : canConnect ? PipeConnectionType.SIDE : PipeConnectionType.NONE;

        return state.with(DIR_TO_CONNECTION.get(direction), finalConnection);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.ADDED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {
        super.onConnectionUpdate(world, state, newState, pos, entity);
//        updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.CHANGED);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient())
            return;

        if (!state.isOf(newState.getBlock()))
        {
//            updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.REMOVED);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.VASCULAR_CONDUIT.instantiate(pos, state);
    }

    @Override
    public VascularConduitEntity getEntity(World world, BlockPos pos, BlockState state)
    {
        return VascularConduitEntity.find(world, pos);
    }
}
