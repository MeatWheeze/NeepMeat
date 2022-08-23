package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.PneumaticPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemInPipe;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PneumaticTubeBlock extends AbstractPipeBlock implements BlockEntityProvider, IItemPipe
{

    public PneumaticTubeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, BaseBlockItem::new, settings);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()))
        {
            if (world.getBlockEntity(pos) instanceof PneumaticPipeBlockEntity be)
            {
                be.dropItems();
            }
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
//        BlockState state2 = enforceApiConnections(world, pos, state);
//        world.setBlockState(pos, state2, Block.NOTIFY_ALL);

        if (!(world.getBlockState(fromPos).getBlock() instanceof PneumaticTubeBlock))
        {
//            createStorageNodes(world, pos, state2);
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockState updatedState = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, updatedState,  Block.NOTIFY_ALL);
//        createStorageNodes(world, pos, updatedState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        boolean forced = type == PipeConnectionType.FORCED;
        boolean otherConnected = false;

        boolean canConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        if (!world.isClient() && !(neighborState.getBlock() instanceof IItemPipe))
        {
            canConnect = canConnect || (canConnectApi((World) world, pos, state, direction));
        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof PneumaticTubeBlock)
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


    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onConnectionUpdate(World world, BlockState state, BlockState newState, BlockPos pos, PlayerEntity entity)
    {
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new PneumaticPipeBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction direction, World world, BlockPos pos)
    {
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            return pipe.connectInDirection(world, pos, state, direction);
        }
        return false;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return state.get(DIR_TO_CONNECTION.get(direction)).canBeChanged();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.PNEUMATIC_PIPE, PneumaticPipeBlockEntity::serverTick, world);
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

    protected boolean canConnectApi(World world, BlockPos pos, BlockState state, Direction direction)
    {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
        return storage != null;
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item)
    {
        if (world.getBlockEntity(pos) instanceof PneumaticPipeBlockEntity be)
        {
            long transferred = be.insert(item, world, state, pos, direction);
            return transferred;
        }
        return 0;
    }
}
