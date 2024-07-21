package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.LadderBlock;

public class MetalRungsBlock extends LadderBlock implements MeatlibBlock
{
    private final String name;

    public MetalRungsBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(name, itemSettings.create(this, ctx, itemSettings));
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
