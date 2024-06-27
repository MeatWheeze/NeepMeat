package com.neep.neepmeat.transport.block.item_transport;

import com.google.common.collect.Iterables;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ItemPipeBlock extends AbstractPipeBlock implements BlockEntityProvider, ItemPipe
{
    public ItemPipeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    protected static boolean matches(ItemStack stack)
    {
        return stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ItemPipeBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()))
        {
            if (world.getBlockEntity(pos) instanceof ItemPipeBlockEntity be)
            {
                be.dropItems();
                be.getCache().update();
            }
            world.removeBlockEntity(pos);
        }
        if (world instanceof ServerWorld serverWorld)
            onChanged(pos, serverWorld);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            onAdded(pos, state, serverWorld);
            if (world.getBlockEntity(pos) instanceof ItemPipeBlockEntity be)
            {
                be.getCache().update();
            }
        }

    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockState updatedState = enforceApiConnections(world, pos, state);
        world.setBlockState(pos, updatedState,  Block.NOTIFY_ALL);

        if (world instanceof ServerWorld serverWorld)
            onAdded(pos, state, serverWorld);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (state.get(WATERLOGGED))
        {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        boolean forced = type == PipeConnectionType.FORCED;
        boolean otherConnected = false;

        boolean canConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
        if (!world.isClient() && !(neighborState.getBlock() instanceof ItemPipe))
        {
            canConnect = canConnect || (canConnectApi((World) world, pos, state, direction));
        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof ItemPipeBlock)
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
        if (world instanceof ServerWorld serverWorld)
            onAdded(pos, state, serverWorld);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ItemPipeBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectTo(BlockState toState, Direction toFace, World world, BlockPos toPos)
    {
        if (toState.getBlock() instanceof ItemPipe pipe)
        {
            return pipe.connectInDirection(world, toPos, toState, toFace);
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
        return MiscUtil.checkType(type, NMBlockEntities.PNEUMATIC_PIPE, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(world1, pos, state1), null, world);
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
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction)
    {
        if (world.getBlockEntity(pos) instanceof ItemPipeBlockEntity be)
        {
            return be.insert(item, world, state, pos, direction, transaction);
        }
        return 0;
    }

    @Override
    public Direction getOutputDirection(ItemInPipe item, BlockPos pos, BlockState state, World world, Direction in, @Nullable BlockEntity beIn, TransactionContext transaction)
    {
//        Set<Direction> connections = ((ItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);
        EnumSet<Direction> connections = ((ItemPipe) state.getBlock()).getConnections(state, d -> true);

        Direction out = item.getPreferredOutputDirection(state, in, this);
        if (out != null && connections.contains(out))
            return out;

        EnumSet<Direction> excluded = EnumSet.noneOf(Direction.class);

        if (beIn instanceof ItemPipeBlockEntity be)
        {
            int initialOut = be.getCurrentOutput(connections);
            int outId;
            do
            {
                outId = be.nextOutput(connections);
                Direction outDir = Direction.values()[outId];
                if (outDir == in)
                    continue;

                var cache = be.getStorageCache(outDir);
                BlockState adjState = world.getBlockState(cache.getPos());
                ItemPipe pipe = ItemPipe.find(adjState);
                if (pipe != null && pipe.prioritise())
                {
                    boolean inserted = pipe.canItemEnter(item.toResourceAmount(), world, cache.getPos(), adjState, outDir.getOpposite());
                    if (inserted)
                        return outDir;

                    excluded.add(outDir);
                    continue;
                }

                Storage<ItemVariant> storage = cache.find(outDir.getOpposite());
                if (storage != null)
                {
                    long inserted = MeatlibStorageUtil.simulateInsert(storage, item.resource(), item.amount(), transaction);

                    if (inserted >= item.amount())
                        return outDir;

                    excluded.add(outDir);
                }
            }
            while (outId != initialOut);
        }

        connections.remove(in);
        connections.removeAll(excluded);
        if (!connections.isEmpty())
            out = Iterables.get(connections, world.getRandom().nextInt(connections.size()));
        else
            out = in;
        return out;
    }

    @Override
    public boolean supportsRouting()
    {
        return true;
    }
}
