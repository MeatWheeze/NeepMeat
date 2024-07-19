package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface AmmunitionProvider
{
    ItemApiLookup<AmmunitionProvider, Context> LOOKUP = ItemApiLookup.get(
            new Identifier(MeatWeapons.NAMESPACE, "ammunition"), AmmunitionProvider.class, Context.class);

    AmmunitionType ammoType();

    int getAmount();

    // Currently, only consuming the entire item is supported.
    void consume();

    interface Context
    {
        void setStack(ItemStack stack);
    }

    record ContextImpl(Inventory inventory, int slot) implements Context
    {
        public void setStack(ItemStack stack)
        {
            inventory.setStack(slot, stack);
        }
    }
}
