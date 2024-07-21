package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseSeedsItem;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BaseCropBlock extends CropBlock implements MeatlibBlock, MeatlibBlockExtension
{
    protected final BaseSeedsItem seedsItem;

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };

    public BaseCropBlock(RegistrationContext ctx, String seedsAppend, int itemMaxStack, int lore, Settings settings)
    {
        super(settings);
        this.seedsItem = new BaseSeedsItem(this, ctx, itemMaxStack, lore);
        ctx.append(this, seedsItem, s -> s + seedsAppend);
    }

    public BaseCropBlock(RegistrationContext ctx, int itemMaxStack, int lore, Settings settings)
    {
        this(ctx, "_seeds", itemMaxStack, lore, settings);
    }

    public BaseCropBlock(RegistrationContext ctx, int itemMaxStack, int lore, ItemFactory factory, Settings settings)
    {
        super(settings);
        this.seedsItem = new BaseSeedsItem(this, ctx, itemMaxStack, lore);
    }

    @Override
    public ItemConvertible dropsLike()
    {
        return null;
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

    public Item getSeedsItemItem()
    {
        return seedsItem;
    }

    @Override
    public void neepmeat$appendTags(TagConsumer<Block> consumer)
    {
        super.neepmeat$appendTags(consumer);
    }
}
