package com.neep.neepmeat.api.big_block;

import com.neep.meatlib.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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
        super(settings);
        this.structureBlock = registerStructureBlock();
    }

    protected abstract T registerStructureBlock();

    protected abstract BlockVolume getVolume();
//    protected BigBlockStructure registerStructureBlock()
//    {
//        return BlockRegistry.queue(new BigBlockStructure(this, FabricBlockSettings.of(Material.METAL)), "obj_test_structure");

//    }

//    protected BlockVolume getVolume()
//    {
//        return VOLUME;
//    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getVolume().toVoxelShape();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        for (var a : getVolume().iterable())
        {
            if (!world.isAir(pos.add(a)))
            {
                return false;
            }
        }

        return super.canPlaceAt(state, world, pos) && world.isSpaceEmpty(getVolume().toBox(pos));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);

        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Vec3i vec : getVolume().iterable())
        {
            mutable.set(pos, vec);

            // Do not replace the origin block
            if (mutable.equals(pos)) continue;

            world.setBlockState(mutable, structureBlock.getDefaultState(), NOTIFY_ALL);
            if (world.getBlockEntity(mutable) instanceof BigBlockStructureEntity be)
            {
                be.setController(pos);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!newState.isOf(this))
        {
            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Vec3i vec : getVolume().iterable())
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
