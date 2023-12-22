package com.neep.neepmeat.transport.api.item_network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.stream.Stream;

public interface RoutablePipe
{
    BlockApiLookup<RoutablePipe, Direction> LOOKUP  = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "routable_pipe"), RoutablePipe.class, Direction.class);

    long requestItem(ItemVariant variant, long amount, NodePos fromPos, TransactionContext transaction);

    Stream<StorageView<ItemVariant>> getAvailable(TransactionContext transaction);
}
