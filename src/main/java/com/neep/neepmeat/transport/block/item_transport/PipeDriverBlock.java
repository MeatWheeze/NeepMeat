package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class PipeDriverBlock extends BaseBlock implements BlockEntityProvider, IItemPipe
{
    public PipeDriverBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public boolean canItemEnter(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction inFace)
    {
        return false;
    }

    @Override
    public boolean canItemLeave(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction outFace)
    {
        return false;
    }

    @Override
    public List<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        return List.of(Direction.values());
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient()) world.getBlockEntity(pos, ItemTransport.PIPE_DRIVER_BE).ifPresent(be -> be.getNetwork(null).invalidate());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (!world.isClient()) world.getBlockEntity(pos, ItemTransport.PIPE_DRIVER_BE).ifPresent(be -> be.getNetwork(null).invalidate());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, ItemTransport.PIPE_DRIVER_BE, PDBlockEntity::serverTick, null, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.PIPE_DRIVER_BE.instantiate(pos, state);
    }

    public static class PDBlockEntity extends SyncableBlockEntity
    {
        protected final RoutingNetwork network = new RoutingNetworkImpl(pos, () -> (ServerWorld) this.getWorld());

        public PDBlockEntity(BlockPos pos, BlockState state)
        {
            this(ItemTransport.PIPE_DRIVER_BE, pos, state);
        }

        public PDBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public RoutingNetwork getNetwork(Void ctx)
        {
            return network;
        }

        public static void serverTick(World world, BlockPos pos, BlockState state, PDBlockEntity be)
        {
            if (be.network.needsUpdate()) be.network.update();
        }
    }
}