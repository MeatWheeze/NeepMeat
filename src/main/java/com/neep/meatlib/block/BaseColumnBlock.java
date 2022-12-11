package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseColumnBlock extends PillarBlock implements IMeatBlock
{
    public final BlockItem blockItem;
    private final String regsitryName;

    public BaseColumnBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(settings);
        this.blockItem = new BaseBlockItem(this, itemName, itemMaxStack, hasLore);
        this.regsitryName = itemName;
    }

    public BaseColumnBlock(String itemName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(settings);
        this.blockItem = factory.get(this, itemName, itemMaxStack, hasLore);
        this.regsitryName = itemName;
    }

    @Override
    public String getRegistryName()
    {
        return regsitryName;
    }

}
