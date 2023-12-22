package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemDuctBlockEntity;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ItemDuctBlock extends AbstractPipeBlock implements BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.FACING;

    public ItemDuctBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, BaseBlockItem::new, settings);

        this.setDefaultState(super.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ItemDuctBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState pipeState = super.getPlacementState(context);

        return pipeState.with(FACING, context.getSide().getOpposite());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, NMBlockEntities.ITEM_DUCT_BLOCK_ENTITY, ItemDuctBlockEntity::serverTick, world);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
//        ItemStack handStack = player.getStackInHand(hand);
//        ((BaseBlock) (NMBlocks.ITEM_DUCT)).getBlockItem();
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            // Scatter contents in world
            if (blockEntity instanceof ItemDuctBlockEntity be)
            {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), be.getResource().toStack((int) be.getAmount()));
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        boolean connection = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);

        // Already connected in direction
        if (connection && (state.get(DIR_TO_CONNECTION.get(direction)).isConnected()))
        {
            return state.with(DIR_TO_CONNECTION.get(direction), PipeConnectionType.SIDE);
        }

        return state.with(DIR_TO_CONNECTION.get(direction), connection ? PipeConnectionType.SIDE : PipeConnectionType.NONE);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof ItemDuctBlockEntity be)
        {
            be.updateApiCache(pos, state);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof ItemDuctBlockEntity be)
        {
            be.updateApiCache(pos, state);
        }
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction direction, World world, BlockPos pos)
    {
        if (state.getBlock() instanceof ItemDuctBlock)
        {
//            return ((FluidAcceptor) state.getBlock()).connectInDirection(state, direction);
            return state.get(FACING) == direction;
        }
        else if (state.getBlock() instanceof HopperBlock)
        {
            return state.get(HopperBlock.FACING) == direction;
        }
        return false;
    }
}
