package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseCraftingItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PinkdrinkItem extends BaseCraftingItem
{
    public PinkdrinkItem(String registryName, int loreLines, Settings settings)
    {
        super(registryName, loreLines, settings);
    }

    @Override
    public SoundEvent getEatSound()
    {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
