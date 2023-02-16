package com.neep.neepmeat.transport.block.item_transport;

import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutablePipe;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.util.BFSGroupFinder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class RoutingNetworkImpl implements RoutingNetwork
{
    protected boolean needsUpdate = true;
    protected final GroupFinder finder = new GroupFinder();

    protected long version;

    protected Supplier<ServerWorld> worldSupplier;
    protected final BlockPos pos;

    protected final ObjectList<BlockApiCache<RoutablePipe, Direction>> routablePipes = new ObjectArrayList<>(10);

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
        ++version;
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
        finder.loop(ItemTransport.BFS_MAX_DEPTH);

        if (finder.controllers > 1) return;

        finder.getResult().forEach((l, c) -> routablePipes.add(c));

        finder.getVisited().forEach(p ->
        {
            if (worldSupplier.get().getBlockEntity(BlockPos.fromLong(p)) instanceof ItemPipeBlockEntity be)
            {
                be.getCache().setNetwork(worldSupplier.get(), RoutingNetworkImpl.this.pos);
            }
        });
    }

    @Override
    public long getVersion()
    {
        return version;
    }

    public List<ResourceAmount<ItemVariant>> getAllAvailable(TransactionContext transaction)
    {
        return routablePipes.stream()
                .map(c -> c.find(null))
                .filter(Objects::nonNull)
                .flatMap(p -> p.getAvailable(transaction))
                .filter(v -> !v.isResourceBlank())
                .map(RoutingNetworkImpl::viewToAmount)
                .collect(Collectors.toList());
    }

    public void request(ResourceAmount<ItemVariant> stack, BlockPos pos, Direction outDir, RequestType type, TransactionContext transaction)
    {
        StoragePreconditions.notBlankNotNegative(stack.resource(), stack.amount());


        try (Transaction inner = transaction.openNested())
        {
            AtomicLong amount = new AtomicLong(stack.amount());
            boolean satisfied = routablePipes.stream().anyMatch(e ->
            {
                long retrieved = e.find(null).requestItem(stack.resource(), amount.get(), new NodePos(pos, Direction.UP), inner);
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

    protected class GroupFinder extends BFSGroupFinder<BlockApiCache<RoutablePipe, Direction>>
    {
        protected int controllers = 0;

        @Override
        public void reset()
        {
            super.reset();
            controllers = 0;
        }

        public Set<Long> getVisited()
        {
            return visited;
        }

        @Override
        protected State processPos(BlockPos pos)
        {
            IItemPipe fromPipe = ItemTransport.ITEM_PIPE.find(worldSupplier.get(), pos, null);

            // Fail if there is a second controller in the network.
            if (checkController(worldSupplier.get(), pos)) return State.FAIL;

            BlockPos.Mutable mutable = pos.mutableCopy();
            for (Direction direction : fromPipe.getConnections(worldSupplier.get().getBlockState(pos), d -> true))
            {
                mutable.set(pos, direction);
                RoutablePipe routablePipe = RoutablePipe.LOOKUP.find(worldSupplier.get(), mutable, null);
                if (routablePipe != null)
                {
                    addResult(mutable, BlockApiCache.create(RoutablePipe.LOOKUP, worldSupplier.get(), mutable));
                }

                IItemPipe toPipe = ItemTransport.ITEM_PIPE.find(worldSupplier.get(), mutable, null);
                if (toPipe != null)
                {
                    queueBlock(mutable);
                }
            }

            return State.CONTINUE;
        }

        protected boolean checkController(ServerWorld world, BlockPos current)
        {
            BlockApiCache<RoutingNetwork, Void> cache = BlockApiCache.create(RoutingNetwork.LOOKUP, (ServerWorld) world, current);
            if (cache.find(null) != null)
            {
                ++controllers;
            }
            return false;
        }
    }
}
