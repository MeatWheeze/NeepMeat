package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class BaseWallBlock extends WallBlock implements MeatlibBlock
{
    protected BlockItem blockItem;

    protected BaseWallBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.getFactory().create(this, ctx, itemSettings);
    }

    public TagKey<Block> getWallTag()
    {
        return BlockTags.WALLS;
    }
}
