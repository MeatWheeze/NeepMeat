package com.neep.meatweapons.meatgun;

import net.minecraft.item.ItemStack;

public class SimpleItemAmmunitionProvider implements AmmunitionProvider
{
    private final ItemStack stack;
    private final Context context;
    private final AmmunitionType type;
    private final int amount;

    public SimpleItemAmmunitionProvider(ItemStack stack, Context context, AmmunitionType type, int amount)
    {
        this.stack = stack;

        this.context = context;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public AmmunitionType ammoType()
    {
        return type;
    }

    @Override
    public int getAmount()
    {
        return amount;
    }

    @Override
    public void consume()
    {
        ItemStack newStack = stack.copy();
        newStack.decrement(1);
        context.setStack(newStack);
    }
}
