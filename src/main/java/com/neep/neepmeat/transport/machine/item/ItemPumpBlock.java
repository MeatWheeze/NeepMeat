package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.machine.content_detector.ContentDetectorBlock;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemInPipe;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemPumpBlock extends BaseFacingBlock implements BlockEntityProvider, IItemPipe
{
    public ItemPumpBlock(String registryName, int itemMaxStack, boolean hasLore, FabricBlockSettings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque().solidBlock(ContentDetectorBlock::never));
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
        return MiscUtils.checkType(type, NMBlockEntities.ITEM_PUMP, ItemPumpBlockEntity::serverTick, world);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be && !world.isClient)
        {
            be.markNeedsRefresh();
            be.updateRedstone(world.isReceivingRedstonePower(pos));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
//        {
//            be.shuttle = 5;
//        }
        if (!world.isClient)
        {
//            ((ItemPumpBlockEntity) world.getBlockEntity(pos)).markNeedsRefresh();
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    // TODO: make this do things
    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item)
    {
        if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
        {
            if (be.getCachedState().get(ItemPumpBlock.FACING) == direction.getOpposite())
            {
                Transaction transaction = Transaction.openOuter();
                long transferred = be.forwardItem(item.getResourceAmount(), transaction);
                transaction.commit();
                return transferred;
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
    public List<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        Direction facing = state.get(FACING);
        return List.of(facing, facing.getOpposite()).stream().filter(forbidden).collect(Collectors.toList());
    }
}