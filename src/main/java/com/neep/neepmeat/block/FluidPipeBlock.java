package com.neep.neepmeat.block;

import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.PipeNetwork;
import com.neep.neepmeat.fluid_transfer.PipeConnectionType;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
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

import java.util.Optional;

public class FluidPipeBlock extends AbstractPipeBlock implements BlockEntityProvider, IFluidPipe
{
    public FluidPipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    public static void removeStorageNodes(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            NodePos nodePos = new NodePos(pos, direction);
            FluidNetwork.getInstance((ServerWorld) world).removeNode(world, nodePos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()))
        {
            removeStorageNodes(world, pos);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        BlockState state2 = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, state2, Block.NOTIFY_ALL);

        if (!(world.getBlockState(fromPos).getBlock() instanceof FluidPipeBlock))
        {
            createStorageNodes(world, pos, state2);
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockState updatedState = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, updatedState,  Block.NOTIFY_ALL);
        createStorageNodes(world, pos, updatedState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        boolean connection = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        if (!world.isClient())
        {
            connection = connection || canConnectApi((World) world, pos, state, direction);
        }

        // Check if neighbour is forced
        boolean neighbourForced = false;
        if (neighborState.getBlock() instanceof FluidPipeBlock)
        {
            neighbourForced = neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.FORCED;
        }

        PipeConnectionType connection1 = neighbourForced
                ? PipeConnectionType.FORCED
                : connection ? PipeConnectionType.SIDE : PipeConnectionType.NONE;

        // I don't know what this bit was for.

        return state.with(DIR_TO_CONNECTION.get(direction), connection1);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {
        createStorageNodes(world, pos, newState);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
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

    public void createStorageNodes(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            for (Direction direction : Direction.values())
            {
                if (state.get(DIR_TO_CONNECTION.get(direction)) == PipeConnectionType.SIDE)
                {
                    FluidNetwork.getInstance(world).updatePosition(world, new NodePos(pos, direction));
                }
                else
                {
                    FluidNetwork.getInstance(world).removeNode(world, new NodePos(pos, direction));
                }
            }
            // TODO: avoid creating instances that will fail immediately
            Optional<PipeNetwork> net = PipeNetwork.tryCreateNetwork((ServerWorld) world, pos, Direction.NORTH);
        }
    }

    private boolean canConnectApi(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
        return storage != null;
    }
}
