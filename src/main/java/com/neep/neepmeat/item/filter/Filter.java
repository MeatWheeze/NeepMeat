package com.neep.neepmeat.item.filter;

import com.neep.meatlib.api.network.ParamCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public interface Filter
{
    boolean matches(ItemVariant variant);
}
