package com.neep.neepmeat.api.block;

import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.item.base.BaseBlockItem;
import net.minecraft.block.Block;

public class BaseBuildingBlock extends Block implements NMBlock
{
    BaseBlockItem blockItem;
    String registryName;

    public BaseBuildingBlock(String blockName, int itemMaxStack, boolean makeWall, Settings settings)
    {
        super(settings);

        BaseStairsBlock stairs = new BaseStairsBlock(this.getDefaultState(),blockName + "_stairs", itemMaxStack, settings);
        BlockInitialiser.BLOCKS.put(stairs.getRegistryName(), stairs);

        BaseSlabBlock slab = new BaseSlabBlock(this.getDefaultState(),blockName + "_slab", itemMaxStack, settings);
        BlockInitialiser.BLOCKS.put(slab.getRegistryName(), slab);

        if (makeWall)
        {
            BaseWallBlock wall = new BaseWallBlock(blockName + "_wall", itemMaxStack, settings);
            BlockInitialiser.BLOCKS.put(wall.getRegistryName(), wall);
        }

        this.registryName = blockName;
        this.blockItem = new BaseBlockItem(this, blockName, itemMaxStack, false);
        BlockInitialiser.BLOCKS.put(getRegistryName(), this);

    }

    public String getRegistryName()
    {
        return registryName;
    }
}