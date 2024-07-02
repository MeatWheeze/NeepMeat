package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.content_detector.InventoryDetectorBlock;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ItemPumpBlock extends EjectorBlock
{
    public ItemPumpBlock(String registryName, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque().solidBlock(InventoryDetectorBlock::never));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be && !world.isClient)
        {
            be.markNeedsRefresh();
            be.updatePowered(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
            MeatlibStorageUtil.scatterAmount(world, pos, be.stored);

        if (world instanceof ServerWorld serverWorld)
            onChanged(pos, serverWorld);

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
        {
            if (be.getCachedState().get(ItemPumpBlock.FACING) == direction.getOpposite())
            {
                return be.forwardItem(item, transaction);
            }
        }
        return 0;
    }
    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return direction.equals(state.get(FACING)) || direction.equals(state.get(FACING).getOpposite());
    }

    @Override
    public EnumSet<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Direction facing = state.get(FACING);
        if (forbidden.test(facing) && forbidden.test(facing.getOpposite()))
            return EnumSet.noneOf(Direction.class);
        else if (!forbidden.test(facing))
            return EnumSet.of(facing.getOpposite());
        else
            return EnumSet.of(facing);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ITEM_PUMP.instantiate(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.ITEM_PUMP, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
