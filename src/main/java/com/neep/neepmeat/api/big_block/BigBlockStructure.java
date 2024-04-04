package com.neep.neepmeat.api.big_block;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public abstract class BigBlockStructure<T extends BigBlockStructureEntity> extends Block implements MeatlibBlock, BlockEntityProvider
{
    protected final BigBlock<?> parent;
    private final BlockEntityType<T> blockEntityType;

    public BigBlockStructure(BigBlock<?> parent, Settings settings, BlockEntityRegisterererer<T> registerBlockEntity)
    {
        super(settings.nonOpaque());
        this.parent = parent;
        this.blockEntityType = registerBlockEntity.get(this);
    }

    public BigBlockStructure(BigBlock<?> parent, Settings settings)
    {
        this(parent, settings, BigBlockStructure::registerBlockEntity);
    }

    protected BlockEntityType<T> registerBlockEntity()
    {
        throw new NotImplementedException("You fool! Implement this or supply a BlockEntityRegistererererer in the super constructor.");
    }

    public BlockEntityType<T> getBlockEntityType()
    {
        return blockEntityType;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (world.getBlockEntity(pos) instanceof BigBlockStructureEntity be)
        {
            BlockPos controllerPos = be.getControllerPos();
            if (controllerPos == null)
                return VoxelShapes.fullCube();

            BlockState parentState = world.getBlockState(controllerPos);
            if (parentState.isOf(parent)) // Sometimes air replaces the parent (not sure why)
               return be.translateShape(parent.getOutlineShape(parentState, world, pos, context));
        }

        return VoxelShapes.empty();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(state.getBlock()))
        {
            // Remove the controller block and let it handle the destruction of the rest of the structure.
            if (world.getBlockEntity(pos) instanceof BigBlockStructureEntity be)
            {
                world.breakBlock(be.getControllerPos(), false);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
    {
//        world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(parent.getDefaultState()));
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
    {
        return 1;
    }

    @Override
    public String getRegistryName()
    {
        throw new NotImplementedException();
    }

    @Override
    public ItemConvertible dropsLike()
    {
        return parent;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return parent.getPickStack(world, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return blockEntityType.instantiate(pos, state);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state)
    {
        return PistonBehavior.IGNORE;
    }

    public static class Simple<T extends BigBlockStructureEntity> extends BigBlockStructure<T>
    {
//        private final BlockEntityRegisterererer<T> registerBlockEntity;

        public Simple(BigBlock<?> parent, Settings settings, BlockEntityRegisterererer<T> registerBlockEntity)
        {
            super(parent, settings, registerBlockEntity);
//            this.registerBlockEntity = registerBlockEntity;
        }

        @Override
        protected BlockEntityType<T> registerBlockEntity()
        {
//            return registerBlockEntity.get(this);
            return null;
        }
    }

    @FunctionalInterface
    public interface BlockEntityRegisterererer<T extends BigBlockStructureEntity>
    {
        BlockEntityType<T> get(BigBlockStructure<T> structure);
    }
}
