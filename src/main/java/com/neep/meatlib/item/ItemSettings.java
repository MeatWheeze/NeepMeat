package com.neep.meatlib.item;

import com.neep.meatlib.block.MeatlibBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

/**
 * An parameter container similar to Item.Settings for use in Block and BlockItem constructors.
 * This exists to ease the addition of new general functionality to every block without having to change
 * constructor arguments.
 */
public class ItemSettings implements MeatlibBlock.ItemFactory
{
    protected int maxCount;
    protected TooltipSupplier tooltipSupplier;
    protected MeatlibBlock.ItemFactory factory;

    protected ItemSettings()
    {
        this.maxCount = 64;
        this.tooltipSupplier = TooltipSupplier.BLANK;
    }

    public static ItemSettings block()
    {
        return new ItemSettings().factory(BaseBlockItem::new);
    }

    public static ItemSettings item()
    {
        return new ItemSettings();
    }

    public ItemSettings maxCount(int maxCount)
    {
        this.maxCount = maxCount;
        return this;
    }

    public ItemSettings tooltip(TooltipSupplier tooltipSupplier)
    {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public ItemSettings factory(MeatlibBlock.ItemFactory factory)
    {
        this.factory = factory;
        return this;
    }

    public MeatlibBlock.ItemFactory getFactory()
    {
        return factory;
    }

    @Override
    public BlockItem create(Block block, String name, ItemSettings settings)
    {
        return factory.create(block, name, settings);
    }
}
