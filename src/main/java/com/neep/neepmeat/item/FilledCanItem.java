package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class FilledCanItem extends BaseItem
{
    public FilledCanItem(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
    {
        super.appendStacks(group, stacks);
    }


}
