package com.neep.neepmeat.transport.api.item_network;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.List;

public interface RoutingNetwork
{
    BlockApiLookup<RoutingNetwork, Void> LOOKUP = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "routing_network"), RoutingNetwork.class, Void.class);

    List<ResourceAmount<ItemVariant>> getAllAvailable(TransactionContext transaction);
    boolean request(ResourceAmount<ItemVariant> stack, BlockPos pos, Direction outDir, RequestType type, TransactionContext transaction);

    void invalidate();

    boolean needsUpdate();

    void update();

    long getVersion();

    boolean isValid();

    RoutingNetwork DEFAULT = new RoutingNetwork()
    {
        @Override
        public List<ResourceAmount<ItemVariant>> getAllAvailable(TransactionContext transaction) { return Collections.emptyList(); }

        @Override
        public boolean request(ResourceAmount<ItemVariant> stack, BlockPos pos, Direction outDir, RequestType type, TransactionContext transaction) { return false; }

        @Override
        public void invalidate() {}

        @Override
        public boolean needsUpdate() { return false; }

        @Override
        public void update() {}

        @Override
        public long getVersion() { return 0;}

        @Override
        public boolean isValid()
        {
            return true;
        }
    };


    enum RequestType
    {
        ANY_AMOUNT(false),
        EXACT_AMOUNT(true);

        private final boolean matchExact;

        RequestType(boolean matchExact)
        {
            this.matchExact = matchExact;
        }

        public boolean satisfied(long requested, long found)
        {
            return requested == found && matchExact || found >= requested && !matchExact;
        }
    }
}
