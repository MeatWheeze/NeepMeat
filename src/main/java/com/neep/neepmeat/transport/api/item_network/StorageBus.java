package com.neep.neepmeat.transport.api.item_network;

import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface StorageBus extends RoutablePipe
{
    void update(ServerWorld world, BlockPos pos);

    List<RetrievalTarget<ItemVariant>> getTargets();
}
