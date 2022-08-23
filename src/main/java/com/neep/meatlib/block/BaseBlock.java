package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseBlock extends Block implements IMeatBlock
{
    public BlockItem blockItem;
    private String registryName;

    public BaseBlock(String registryName, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, registryName, 64, false);
        this.registryName = registryName;
        addTags();
    }

    public BaseBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, registryName, itemMaxStack, hasLore);
        this.registryName = registryName;
        addTags();
    }

    public BaseBlock(String registryName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(settings);
        this.blockItem = factory.get(this, registryName, itemMaxStack, hasLore);
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
