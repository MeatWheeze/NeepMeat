package com.neep.meatlib.block;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.Block;

public class BaseBuildingBlock extends Block implements IMeatBlock
{
    BaseBlockItem blockItem;
    String registryName;

    public BaseBuildingBlock(String blockName, int itemMaxStack, boolean makeWall, Settings settings)
    {
        super(settings);

        BaseStairsBlock stairs = new BaseStairsBlock(this.getDefaultState(),blockName + "_stairs", itemMaxStack, settings);
        BlockRegistry.queue(stairs);

        BaseSlabBlock slab = new BaseSlabBlock(this.getDefaultState(),blockName + "_slab", itemMaxStack, settings);
        BlockRegistry.queue(slab);

        if (makeWall)
        {
            BaseWallBlock wall = new BaseWallBlock(blockName + "_wall", itemMaxStack, settings);
            BlockRegistry.queue(wall);
        }

        this.registryName = blockName;
        this.blockItem = new BaseBlockItem(this, blockName, itemMaxStack, false);
        BlockRegistry.queue(this);

    }

    public String getRegistryName()
    {
        return registryName;
    }
}