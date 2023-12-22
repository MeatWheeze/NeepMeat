package com.neep.neepmeat.block.base;

import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PipeBlock;
import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.fluid_util.FluidNetwork;
import com.neep.neepmeat.fluid_util.NMFluidNetwork;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.node.NodePos;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidPipeBlock extends PipeBlock implements BlockEntityProvider
{
    public FluidPipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        BlockState state2 = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, state2, Block.NOTIFY_ALL);

        // Dirty bodge for now. Might change if it works.
        createStorageNodes(world, pos, state2);

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockState updatedState = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, updatedState,  Block.NOTIFY_ALL);
        createStorageNodes(world, pos, updatedState);
    }

    @Override
    // TODO: enforce api connections
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        boolean connection = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        if (!world.isClient())
        {
            connection = connection || canConnectApi((World) world, pos, state, direction);
        }

        if (connection == state.get(DIR_TO_CONNECTION.get(direction)) && !isFullyConnected(state))
        {
            return state.with(DIR_TO_CONNECTION.get(direction), connection);
        }

        return state.with(DIR_TO_CONNECTION.get(direction), connection);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
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
                    state2 = state2.with(DIR_TO_CONNECTION.get(direction), true);
                }
            }
            return state2;
        }
        return state;
    }

    // TODO: Major code reduction may be possible
    public void createStorageNodes(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            for (Direction direction : Direction.values())
            {
                Storage<FluidVariant> storage;
                if ((storage = FluidStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite())) != null
                        && state.get(DIR_TO_CONNECTION.get(direction)))
                {
                    FluidNode node;
                    BlockState nextPos = world.getBlockState(pos.offset(direction));
                    if (nextPos.getBlock() instanceof FluidNodeProvider provider)
                    {
                        node = new FluidNode(pos, direction, storage, provider.getDirectionMode(nextPos, direction.getOpposite()), 2);
                    }
                    else
                    {
                        node = new FluidNode(pos, direction, storage, AcceptorModes.INSERT_EXTRACT, 0);
                    }
                    updateNetwork(world, pos, node, false);
                } else
                {
                    FluidNetwork.INSTANCE.removeNode(world, new NodePos(pos, direction));
                }
            }
            // TODO: avoid creating that will fail immediately
            Optional<NMFluidNetwork> net = NMFluidNetwork.tryCreateNetwork(world, pos, Direction.NORTH);
        }
    }

    private boolean canConnectApi(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
        return storage != null;
    }
}
