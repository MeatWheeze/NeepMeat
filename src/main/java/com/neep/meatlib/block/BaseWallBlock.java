package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class BaseWallBlock extends WallBlock implements MeatlibBlock
{
    protected String registryName;
    protected BlockItem blockItem;

    protected BaseWallBlock(String blockName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = blockName;
        this.blockItem = itemSettings.getFactory().create(this, blockName, itemSettings);
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
