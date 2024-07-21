package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseBlock extends Block implements MeatlibBlock
{
    public final BlockItem blockItem;

    public BaseBlock(RegistrationContext ctx, Settings settings)
    {
        this(ctx, ItemSettings.block(), settings);
//        super(settings);
//        this.blockItem = new BaseBlockItem(this, registryName, ItemSettings.block());
//        this.registryName = registryName;
//        addTags();
    }

    public BaseBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.getFactory().create(this, ctx, itemSettings);
    }

    public Item getBlockItem()
    {
        return blockItem;
    }
}
