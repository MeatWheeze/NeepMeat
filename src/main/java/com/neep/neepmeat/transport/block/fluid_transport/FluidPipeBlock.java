package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.PipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FluidPipeBlock extends AbstractPipeBlock implements BlockEntityProvider, IFluidPipe
{
    public FluidPipeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient())
            return;

        if (!state.isOf(newState.getBlock()))
        {
            removePipe((ServerWorld) world, state, pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        Direction direction = Direction.fromVector(fromPos.subtract(pos));
        BlockState nextState = getStateForNeighborUpdate(state, direction, world.getBlockState(fromPos), world, pos, fromPos);

        // Block state change must be applied to the world in order for PipeNetwork::discoverNodes to pick it up
        world.setBlockState(pos, nextState, Block.NOTIFY_LISTENERS);

//        if (!(world.getBlockState(fromPos).getBlock() instanceof FluidPipeBlock))
        if (IFluidPipe.findFluidPipe(world, fromPos, world.getBlockState(fromPos)).isEmpty())
        {
            if (createStorageNodes(world, pos, nextState))
                updateNetwork((ServerWorld) world, pos, nextState, PipeNetwork.UpdateReason.NODE_CHANGED);
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockState updatedState = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, updatedState,  Block.NOTIFY_ALL);
        if (!world.isClient())
        {
            createStorageNodes(world, pos, updatedState);
            updateNetwork((ServerWorld) world, pos, state, PipeNetwork.UpdateReason.PIPE_ADDED);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        boolean forced = type == PipeConnectionType.FORCED;
        boolean otherConnected = false;

        boolean canConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        if (world instanceof ServerWorld serverWorld && !(neighborState.getBlock() instanceof FluidPipeBlock))
        {

            canConnect = canConnect || (canConnectApi((World) world, pos, state, direction));
        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof FluidPipeBlock)
        {
            forced = forced || neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.FORCED;
            otherConnected = neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.SIDE;

        }

        // AAAAAAAAAAAAAAAAAAAA
        PipeConnectionType finalConnection =
                otherConnected ? PipeConnectionType.SIDE :
                        forced ? PipeConnectionType.FORCED
                                : canConnect ? PipeConnectionType.SIDE : PipeConnectionType.NONE;

        BlockState finalState = state.with(DIR_TO_CONNECTION.get(direction), finalConnection);


        return finalState;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {
        if (world.isClient())
            return;

        createStorageNodes(world, pos, newState);
        updateNetwork((ServerWorld) world, pos, state, PipeNetwork.UpdateReason.CONNECTION_CHANGED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLUID_PIPE.instantiate(pos, state);
    }

    // Only takes into account other pipes, connections to storages are enforced later.
    @Override
    public boolean canConnectTo(BlockState state, Direction direction, World world, BlockPos pos)
    {
        if (state.getBlock() instanceof IFluidPipe)
        {
            return ((IFluidPipe) state.getBlock()).connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    // Creates blockstate connections to fluid containers after placing
    private BlockState enforceApiConnections(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            BlockState state2 = state;
            for (Direction direction : Direction.values())
            {
                if (canConnectApi(world, pos, state, direction))
                {
                    state2 = state2.with(DIR_TO_CONNECTION.get(direction), PipeConnectionType.SIDE);
                }
            }
            return state2;
        }
        return state;
    }

    private boolean canConnectApi(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
        return storage != null;
    }
}