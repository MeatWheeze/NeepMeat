package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class ContaminatedDirtBlock extends BaseBlock
{
    public ContaminatedDirtBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public TagKey<Block> getPreferredTool()
    {
        return BlockTags.SHOVEL_MINEABLE;
    }
}
