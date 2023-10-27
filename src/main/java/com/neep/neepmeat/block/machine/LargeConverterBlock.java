package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.machine.converter.ConverterBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class LargeConverterBlock extends BaseBlock implements BlockEntityProvider
{
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public LargeConverterBlock(String registryName, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
        this.setDefaultState(getDefaultState().with(TYPE, Type.UNASSEMBLED));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.isSneaking())
        {
            System.out.println(world.getBlockEntity(pos));
            return ActionResult.SUCCESS;
        }
        boolean valid = checkValid(world, pos);
        if (valid)
        {
            BlockPos corner = findCorner(world, pos, this, LargeConverterBlock::notAssembled, 1);

            BlockPos.Mutable mutable = corner.mutableCopy();
            for (int dx = 0; dx <= 1; ++dx)
            {
                for (int dz = 0; dz <= 1; ++dz)
                {
                    mutable.set(corner.add(dx, 0, dz));
                    world.setBlockState(mutable, this.getDefaultState().with(TYPE, Type.BOTTOM), Block.FORCE_STATE);
                    mutable.set(corner.add(dx, 1, dz));
                    world.setBlockState(mutable, this.getDefaultState().with(TYPE, Type.TOP), Block.FORCE_STATE);
                }
            }
            world.setBlockState(corner, this.getDefaultState().with(TYPE, Type.ASSEMBLED), Block.FORCE_STATE);

            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!world.isClient && newState.isAir() && state.get(TYPE).isAssembled())
        {
            disassemble(world, pos, state);
        }
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return state.get(TYPE).isInvisible() ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(TYPE);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.LARGE_CONVERTER, ConverterBlockEntity::serverTick, null, world);
    }

    public boolean checkValid(World world, BlockPos pos)
    {
        BlockPos corner = findCorner(world, pos, this, LargeConverterBlock::notAssembled, 1);
        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int dx = 0; dx <= 1; ++dx)
        {
            for (int dy = 0; dy <= 1; ++dy)
            {
                for (int dz = 0; dz <= 1; ++dz)
                {
                    mutable.set(corner.add(dx, dy, dz));
                    BlockState state = world.getBlockState(mutable);
                    if (!(state.isOf(this) && state.get(TYPE) == Type.UNASSEMBLED))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void disassemble(World world, BlockPos pos, BlockState currentState)
    {
        BlockPos corner = findCorner(world, pos, this, LargeConverterBlock::isAssembled, 1);

        BlockPos.Mutable mutable = corner.mutableCopy();
        for (int dx = 0; dx <= 1; ++dx)
        {
            for (int dy = 0; dy <= 1; ++dy)
            {
                for (int dz = 0; dz <= 1; ++dz)
                {
                    mutable.set(corner.add(dx, dy, dz));
                    if (world.getBlockState(mutable).isOf(this))
                    {
                        world.setBlockState(mutable, this.getDefaultState().with(TYPE, Type.UNASSEMBLED));
                    }
                }
            }
        }
    }

    public static BlockPos findCorner(World world, BlockPos pos, Block block, Predicate<BlockState> stateFilter, int limit)
    {
        BlockPos yEnd = findColumnEnd(world, pos, block, stateFilter, Direction.DOWN, limit);
        BlockPos xEnd = findColumnEnd(world, yEnd, block, stateFilter, Direction.WEST, limit);
        BlockPos corner = findColumnEnd(world, xEnd, block, stateFilter, Direction.NORTH, limit);

        return corner;
    }

    public static BlockPos findColumnEnd(World world, BlockPos pos, Block block, Predicate<BlockState> stateFilter, Direction direction, int limit)
    {
        BlockState blockState;
        BlockPos.Mutable mutable = pos.mutableCopy();
        int level = 1;
        while ((blockState = world.getBlockState(mutable.offset(direction))).isOf(block)
        && level <= limit
        && stateFilter.test(blockState))
        {
            ++level;
            mutable.move(direction);
        }
        return mutable;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        if (isAssembled(state))
        {
//            return NMBlockEntities.LARGE_CONVERTER.instantiate(pos, state);
        }
        return null;
    }

    public static boolean notAssembled(BlockState state)
    {
        return !state.get(TYPE).isAssembled();
    }

    public static boolean isAssembled(BlockState state)
    {
        return state.get(TYPE).isAssembled();
    }

    public enum Type implements StringIdentifiable
    {
        UNASSEMBLED("unassembled"),
        ASSEMBLED("assembled"),
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        @Override
        public String asString()
        {
            return name;
        }

        public boolean isAssembled()
        {
            return this != UNASSEMBLED;
        }

        public boolean isInvisible()
        {
            return this == TOP || this == BOTTOM;
        }

    }
}
