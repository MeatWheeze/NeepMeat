package com.neep.neepmeat.machine.small_trommel;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

public interface TrommelStorage
{
    SingleVariantStorage<FluidVariant> input();

    Storage<FluidVariant> output();

    Storage<ItemVariant> itemOutput();
}
