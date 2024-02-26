package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ChuteBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    private final VoxelShape NORTH_CLOSED = Block.createCuboidShape(1, 1, 0, 15, 15, 2);
    private final VoxelShape NORTH_OPEN = Block.createCuboidShape(1, 1, 0, 15, 15, 7);

    private final Map<Direction, VoxelShape> closedShapeMap = Map.of(
            Direction.NORTH, MiscUtil.rotateShapeY(NORTH_CLOSED, 0),
            Direction.EAST, MiscUtil.rotateShapeY(NORTH_CLOSED, 90),
            Direction.SOUTH, MiscUtil.rotateShapeY(NORTH_CLOSED, 180),
            Direction.WEST, MiscUtil.rotateShapeY(NORTH_CLOSED, 270)
    );

    private final Map<Direction, VoxelShape> openShapeMap = Map.of(
            Direction.NORTH, MiscUtil.rotateShapeY(NORTH_OPEN, 0),
            Direction.EAST, MiscUtil.rotateShapeY(NORTH_OPEN, 90),
            Direction.SOUTH, MiscUtil.rotateShapeY(NORTH_OPEN, 180),
            Direction.WEST, MiscUtil.rotateShapeY(NORTH_OPEN, 270)
    );

    public ChuteBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
        setDefaultState(getDefaultState().with(OPEN, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (state.get(OPEN))
        {
            return openShapeMap.get(state.get(FACING));
        }
        else
        {
            return closedShapeMap.get(state.get(FACING));
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {
        if (!world.isClient() && state.get(OPEN) && world.getTime() % 5 == 0)
        {
            if (world.getBlockEntity(pos) instanceof ChuteBlockEntity be && entity instanceof ItemEntity itemEntity)
            {
                be.eat(itemEntity);
            }
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        Direction side = context.getSide().getOpposite();
        if (side.getAxis().isVertical())
        {
            BlockPos.Mutable mutable = context.getBlockPos().mutableCopy();
            for (Direction direction : Direction.values())
            {
                if (direction.getAxis().isVertical())
                    continue;

                mutable.set(context.getBlockPos(), direction);
                if (!context.getWorld().getBlockState(mutable).isAir())
                {
                    return getDefaultState().with(FACING, direction);
                }
            }
            return getDefaultState();
        }
        return getDefaultState().with(FACING, side);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return canExist(world, pos, state) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!canExist(world, pos, state))
        {
            world.breakBlock(pos, true);
        }
    }

    public static boolean canExist(WorldView world, BlockPos pos, BlockState state)
    {
        BlockPos backPos = pos.offset(state.get(FACING));
        return !world.getBlockState(backPos).isAir();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        boolean open = state.get(OPEN);
        world.playSound(null, pos, open ? SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE : SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
        world.setBlockState(pos, state.cycle(OPEN));
        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(OPEN);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CHUTE.instantiate(pos, state);
    }

    public static class ChuteBlockEntity extends BlockEntity
    {
        private final LazyBlockApiCache<Storage<ItemVariant>, Direction> cache = LazyBlockApiCache.of(
                ItemStorage.SIDED, pos.offset(getCachedState().get(FACING)), this::getWorld, () -> getCachedState().get(FACING));

        public ChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public void eat(ItemEntity itemEntity)
        {
            Storage<ItemVariant> storage = cache.find();

            if (storage == null)
                return;

            try (Transaction transaction = Transaction.openOuter())
            {
                ItemStack stack = itemEntity.getStack();
                long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                if (stack.isEmpty())
                {
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);
                    transaction.commit();
                    return;
                }
                itemEntity.setStack(stack);
                transaction.commit();
            }
        }
    }
}
