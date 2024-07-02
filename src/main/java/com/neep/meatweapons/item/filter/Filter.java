package com.neep.meatweapons.item.filter;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

interface Filter
{
    boolean matches(ItemVariant variant, long amount);
}
