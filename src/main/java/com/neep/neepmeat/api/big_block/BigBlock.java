package com.neep.neepmeat.api.big_block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class BigBlock<T extends BigBlockStructure<?>> extends Block
{
//    public static final BlockVolume VOLUME = BlockVolume.oddCylinder(1, 0, 2);

    private final T structureBlock;

    public BigBlock(Settings settings)
    {
        super(settings.pistonBehavior(PistonBehavior.IGNORE));
        this.structureBlock = registerStructureBlock();
    }

    protected abstract T registerStructureBlock();

    protected abstract BigBlockPattern getVolume(BlockState blockState);

//    protected VoxelShape getShape(BlockState state)
//    {
//        return getVolume(state).toVoxelShape();
//    }
//    protected BigBlockStructure registerStructureBlock()
//    {
//        return BlockRegistry.queue(new BigBlockStructure(this, FabricBlockSettings.of(Material.METAL)), "obj_test_structure");

//    }

//    protected BlockVolume getVolume()
//    {
//        return VOLUME;
//    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state)
    {

        super.spawnBreakParticles(world, player, pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getVolume(state).toVoxelShape();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        for (var a : getVolume(state).iterable())
        {
            if (!world.isAir(pos.add(a)))
            {
                return false;
            }
        }

        for (var box : getVolume(state).toVoxelShape().getBoundingBoxes())
        {
            if (!world.isSpaceEmpty(box.offset(pos)))
                return false;
        }

        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);

        getVolume(state).placeBlocks(world, pos, pos);
//        BlockPos.Mutable mutable = pos.mutableCopy();
//        for (Vec3i vec : getVolume(state).iterable())
//        {
//            mutable.set(pos, vec);
//
//            // Do not replace the origin block
//            if (mutable.equals(pos)) continue;
//
//            world.setBlockState(mutable, structureBlock.getDefaultState(), NOTIFY_ALL);
//            if (world.getBlockEntity(mutable) instanceof BigBlockStructureEntity be)
//            {
//                be.setController(pos);
//            }
//        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!newState.isOf(this))
        {
            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Vec3i vec : getVolume(state).iterable())
            {
                mutable.set(pos, vec);
                world.setBlockState(mutable, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            }
        }
    }

    public T getStructure()
    {
        return structureBlock;
    }
}
