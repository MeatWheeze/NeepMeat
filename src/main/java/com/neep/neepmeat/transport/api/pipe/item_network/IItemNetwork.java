package com.neep.neepmeat.transport.api.pipe.item_network;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface IItemNetwork
{
    boolean retrieve(BlockPos to, Direction in, ItemVariant variant, long amount);

    long eject(BlockPos from, Direction out, ItemVariant variant, long amount);
}
