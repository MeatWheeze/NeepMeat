package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class BaseWallBlock extends WallBlock implements IMeatBlock
{
    protected String registryName;
    protected int maxStack;
    protected BlockItem blockItem;

    protected BaseWallBlock(String blockName, int itemMaxStack, Settings settings)
    {
        super(settings);
        this.registryName = blockName;
        this.maxStack = itemMaxStack;
        this.blockItem = new BaseBlockItem(this, blockName, itemMaxStack, false);
    }

    public TagKey<Block> getWallTag()
    {
        return BlockTags.WALLS;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
