package com.neep.neepmeat.api.big_block;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class BigBlock extends Block implements MeatlibBlock
{
    private final String registryName;
    private final BigBlockStructure structureBlock;

    public BigBlock(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.structureBlock = createStructure();
    }

    protected abstract BigBlockStructure createStructure();
    protected abstract BlockVolume getVolume();

    public BigBlockStructure getStructure()
    {
        return structureBlock;
    }

    protected abstract BlockEntityType<? extends BigBlockStructureBlockEntity> getBlockEntityType();

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Box box = getVolume().toBox(pos);
        return world.isSpaceEmpty(box) && super.canPlaceAt(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Vec3i vec : getVolume().iterable())
        {
            mutable.set(pos, vec);

            // Do not replace the origin block
            if (mutable.equals(pos)) continue;

            world.setBlockState(mutable, structureBlock.getDefaultState(), NOTIFY_ALL);
            if (world.getBlockEntity(mutable) instanceof BigBlockStructureBlockEntity be)
            {
                be.setController(pos);
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this))
        {
            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Vec3i vec : getVolume().iterable())
            {
                mutable.set(pos, vec);
                world.setBlockState(mutable, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

}
