package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseSeedsItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BaseCropBlock extends CropBlock implements IMeatBlock
{
    protected final String registryName;
    protected final Item seedsItem;

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };

    public BaseCropBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.seedsItem = new BaseSeedsItem(this, registryName + "_seeds", itemMaxStack, hasLore);
        this.registryName = registryName;
    }

    public BaseCropBlock(String registryName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(settings);
        this.seedsItem = new BaseSeedsItem(this, registryName, itemMaxStack, hasLore);
        this.registryName = registryName;
    }

    @Override
    public boolean dropsSelf()
    {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }

    @Override
    public ItemConvertible getSeedsItem()
    {
        return seedsItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
