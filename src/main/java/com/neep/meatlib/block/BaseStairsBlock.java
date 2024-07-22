package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;

public class BaseStairsBlock extends StairsBlock implements MeatlibBlock
{
    protected BlockItem blockItem;
    private Block baseBlock;

    public BaseStairsBlock(RegistrationContext ctx, BlockState baseBlockState, ItemSettings itemSettings, Settings settings)
    {
        super(baseBlockState, settings);
//        System.out.println(this.hashCode() + ", " + baseBlockState.getBlock().getClass());
        this.baseBlock = baseBlockState.getBlock();
        this.blockItem = itemSettings.create(this, ctx, itemSettings);
    }

    @Override
    public String toString()
    {
        return super.toString() + baseBlock.getClass();
    }
}
