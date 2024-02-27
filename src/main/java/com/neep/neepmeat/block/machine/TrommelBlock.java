package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.trommel.TrommelBlockEntity;
import com.neep.neepmeat.machine.trommel.TrommelStructureBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TrommelBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public final VoxelShape[] SHAPES = {
            VoxelShapes.cuboid(0f, 0d, 0d, 1d, 1d, 1d),
            VoxelShapes.cuboid(0f, 0d, 0d, 1d, 1d, 1d)
    };

    public TrommelBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
//        this.setDefaultState(getDefaultState().with(CENTRE, false).with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPES[(state.get(FACING).getAxis().equals(Direction.Axis.X)) ? 0 : 1];
    }

    @Deprecated
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return state.getOutlineShape(world, pos);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
    {
        return true;
    }

    public static boolean placeStructure(World world, BlockPos pos, TrommelBlockEntity controller, BlockPos origin)
    {
        if (!world.isAir(pos)) return false;

//        world.setBlockState(pos, NMBlocks.TROMMEL_STRUCTURE.getDefaultState(), Block.NOTIFY_LISTENERS);
        if (world.getBlockEntity(pos) instanceof TrommelStructureBlockEntity be)
        {
            be.setController(origin, controller.getCachedState().get(FACING).getOpposite());
            controller.addStructure(be);
        }
        return true;
    }

    public static Iterable<BlockPos> getVolume(WorldView world, BlockPos origin, Direction facing)
    {
        BlockPos pos1 = origin.offset(facing).offset(facing.rotateYCounterclockwise());
        BlockPos pos2 = origin.up().offset(facing.rotateYClockwise());
        return BlockPos.iterate(pos1, pos2);
    }

    public static boolean checkVolume(WorldView world, BlockPos origin, Direction facing)
    {
        for (BlockPos p : getVolume(world, origin, facing))
        {
//            if (!(world.isAir(p) || world.getBlockState(p).isOf(NMBlocks.TROMMEL))) return false;
        }
        return true;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return super.canPlaceAt(state, world, pos) && checkVolume(world, pos, state.get(FACING).getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        Direction facing = state.get(FACING).getOpposite();
        if (world.getBlockEntity(pos) instanceof TrommelBlockEntity be)
        {
            for (BlockPos p : getVolume(world, pos, facing))
            {
                if (world.isAir(p)) placeStructure(world, p, be, pos);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);
        Direction facing = state.get(FACING).getOpposite();
        for (BlockPos p : getVolume(world, pos, facing))
        {
//            if (world.getBlockState(p).isOf(NMBlocks.TROMMEL_STRUCTURE))
            {
                world.setBlockState(p, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TrommelBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    public static class Structure extends Block implements MeatlibBlock, BlockEntityProvider
    {
        private final String registryName;

        public Structure(String registryName, Settings settings)
        {
            super(settings.nonOpaque());
            this.registryName = registryName;
        }

        @Override
        public ItemConvertible dropsLike()
        {
            return Blocks.AIR;
//            return NMBlocks.TROMMEL;
        }

        @Override
        public String getRegistryName()
        {
            return registryName;
        }

        @Override
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.INVISIBLE;
        }

        @Override
        public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
        {
            return true;
        }

        @Override
        public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
        {
            return 1.0f;
        }

//        @Override
//        public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
//        {
//            return NMBlocks.TROMMEL.getPickStack(world, pos, state);
//        }

        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
        {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof TrommelStructureBlockEntity be)
            {
                be.signalBroken((ServerWorld) world);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }

        @Nullable
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
        {
            return NMBlockEntities.TROMMEL_STRUCTURE.instantiate(pos, state);
        }
    }

}
