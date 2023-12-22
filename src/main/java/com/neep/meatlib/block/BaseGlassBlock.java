package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseGlassBlock extends AbstractGlassBlock implements IMeatBlock
{
    public BlockItem blockItem;
    private String registryName;

    public BaseGlassBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, registryName, itemMaxStack, hasLore);
        this.registryName = registryName;
    }

    public BaseGlassBlock(String registryName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(settings);
        this.blockItem = factory.get(this, registryName, itemMaxStack, hasLore);
        this.registryName = registryName;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
