package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseBlock extends Block implements MeatlibBlock
{
    public final BlockItem blockItem;
    private final String registryName;

    public BaseBlock(String registryName, Settings settings)
    {
        this(registryName, ItemSettings.block(), settings);
//        super(settings);
//        this.blockItem = new BaseBlockItem(this, registryName, ItemSettings.block());
//        this.registryName = registryName;
//        addTags();
    }

    public BaseBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.getFactory().create(this, registryName, itemSettings);
        this.registryName = registryName;
        addTags();
    }

    public Item getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
