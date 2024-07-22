package com.neep.meatlib.item;


import com.neep.meatlib.registry.RegistrationContext;

public class BaseCraftingItem extends BaseItem implements MeatlibItem
{
    public BaseCraftingItem(RegistrationContext ctx, int loreLines, Settings settings)
    {
        super(TooltipSupplier.simple(loreLines), settings);
    }
}
