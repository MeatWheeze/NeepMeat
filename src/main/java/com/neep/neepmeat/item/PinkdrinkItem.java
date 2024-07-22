package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.registry.RegistrationContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class PinkdrinkItem extends BaseCraftingItem
{
    public PinkdrinkItem(RegistrationContext ctx, int loreLines, Settings settings)
    {
        super(ctx, loreLines, settings);
    }

    @Override
    public SoundEvent getEatSound()
    {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
}
