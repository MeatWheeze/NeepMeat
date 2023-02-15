package com.neep.neepmeat.transport.block.item_transport;

import com.google.common.collect.Streams;
import com.neep.neepmeat.machine.item_mincer.ItemMincerBlockEntity;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutablePipe;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.util.BFSGroupFinder;
import com.neep.neepmeat.util.DFSFinder;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class RoutingNetworkImpl implements RoutingNetwork
{
    protected boolean needsUpdate;
    protected final GroupFinder finder = new GroupFinder();

    protected Supplier<ServerWorld> worldSupplier;
    protected final BlockPos pos;

    protected final Long2ObjectOpenHashMap<RoutablePipe> routablePipes = new Long2ObjectOpenHashMap<>();

    public RoutingNetworkImpl(BlockPos pos, Supplier<ServerWorld> worldSupplier)
    {
        this.pos = pos;
        this.worldSupplier = worldSupplier;
    }

    public static <T> ResourceAmount<T> viewToAmount(StorageView<T> view)
    {
        return new ResourceAmount<>(view.getResource(), view.getAmount());
    }

    @Override
    public void invalidate()
    {
        needsUpdate = true;
    }

    @Override
    public boolean needsUpdate()
    {
        return needsUpdate;
    }

    @Override
    public void update()
    {
        needsUpdate = false;

        routablePipes.clear();
        finder.reset();
        finder.queueBlock(pos);
        finder.loop(50);
        routablePipes.putAll(finder.getResult());
    }

    public List<ResourceAmount<ItemVariant>> getAllAvailable(TransactionContext transaction)
    {
        return routablePipes.values().stream()
                .flatMap(p -> p.getAvailable(transaction))
                .map(RoutingNetworkImpl::viewToAmount)
                .collect(Collectors.toList());
    }

    public void request(ResourceAmount<ItemVariant> stack, BlockPos pos, Direction outDir, RequestType type, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(stack.resource(), stack.amount());


        try (Transaction inner = transaction.openNested())
        {
            AtomicLong amount = new AtomicLong(stack.amount());
            boolean satisfied = routablePipes.long2ObjectEntrySet().stream().anyMatch(e ->
            {
                long retrieved = e.getValue().requestItem(stack.resource(), amount.get(), new NodePos(pos, Direction.UP), inner);
                amount.addAndGet(-retrieved);
                return amount.get() <= 0;
            });

            if (type.satisfied(stack.amount(), stack.amount() - amount.get()))
            {
                inner.commit();
            }
            else inner.abort();
        }
    }

    protected class GroupFinder extends BFSGroupFinder<RoutablePipe>
    {
        @Override
        protected State processPos(BlockPos pos)
        {
            IItemPipe fromPipe = ItemTransport.ITEM_PIPE.find(worldSupplier.get(), pos, null);

            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Direction direction : fromPipe.getConnections(worldSupplier.get().getBlockState(pos), d -> true))
            {
                mutable.set(pos, direction);
                RoutablePipe routablePipe = RoutablePipe.LOOKUP.find(worldSupplier.get(), mutable, null);
                if (routablePipe != null)
                {
                    addResult(mutable, routablePipe);
                }

                IItemPipe toPipe = ItemTransport.ITEM_PIPE.find(worldSupplier.get(), mutable, null);
                if (toPipe != null)
                {
                    queueBlock(mutable);
                }
            }

            return State.CONTINUE;
        }
    }
}
