package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;

public class BaseSeedsItem extends AliasedBlockItem implements IMeatItem
{
    protected final String registryName;

    public BaseSeedsItem(Block block, String registryName, int maxCount, boolean hasLore)
    {
        super(block, new FabricItemSettings().maxCount(maxCount).group(NMItemGroups.GENERAL));
        this.registryName = registryName;
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
