package com.neep.neepmeat.transport.block.item_transport.entity;

import com.google.common.collect.Streams;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.item_network.StorageBus;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.item_network.RoutingNetworkDFSFinder;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class StorageBusBlockEntity extends ItemPipeBlockEntity implements StorageBus
{
    protected BlockApiCache<RoutingNetwork, Void> controller;
    protected final List<RetrievalTarget<ItemVariant>> targets = new ArrayList<>(6);
    protected boolean needsUpdate = true;

    public StorageBusBlockEntity(BlockPos pos, BlockState state)
    {
        this(ItemTransport.STORAGE_BUS_BE, pos, state);
    }

    public StorageBusBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void setController(BlockApiCache<RoutingNetwork, Void> controller)
    {
        this.controller = controller;
    }

    public RoutingNetwork getController()
    {
        if (controller == null) controller = BlockApiCache.create(RoutingNetwork.LOOKUP, (ServerWorld) world, pos);
        return controller.find(null);
    }

    protected void updateTargets()
    {
        targets.clear();

        BlockPos.Mutable mutable = getPos().mutableCopy();
        for (Direction direction : Direction.values())
        {
            mutable.set(getPos(), direction);
            BlockApiCache<Storage<ItemVariant>, Direction> storageCache = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, mutable);
            if (storageCache.find(direction.getOpposite()) != null)
            {
                targets.add(RetrievalTarget.of(storageCache, direction.getOpposite()));
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
//        NbtList posList = new NbtList();
//        targets.forEach(t ->
//        {
//            NbtCompound compound = NbtHelper.fromBlockPos(t.getPos());
//            compound.putInt("direction", t.getFace().ordinal());
//            posList.add(compound);
//        });
//        nbt.put("storage_pos", posList);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }

    @Override
    public void update(ServerWorld world, BlockPos pos)
    {
        RoutingNetworkDFSFinder finder = new RoutingNetworkDFSFinder(world);
        finder.pushBlock(pos, Direction.UP);
        finder.loop(50);
        if (finder.hasResult())
        {
            setController(finder.getResult().right());
        }

        updateTargets();
    }

    @Override
    public List<RetrievalTarget<ItemVariant>> getTargets()
    {
        if (needsUpdate)
        {
            updateTargets();
//            if (getWorld() != null && getWorld() instanceof ServerWorld)
//            {
//                targets.clear();
//                storageFaces.forEach(p -> targets.add(new RetrievalTarget<>(BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) getWorld(), p.first()), p.second())));
                needsUpdate = false;
//            }
        }
        return targets;
    }

    @Override
    public long requestItem(ItemVariant variant, long amount, NodePos fromPos, TransactionContext transaction)
    {
        List<Pair<RetrievalTarget<ItemVariant>, Long>> foundTargets = new ArrayList<>(6);

        long remaining = amount;
        for (RetrievalTarget<ItemVariant> target : getTargets())
        {
            Storage<ItemVariant> storage = target.find();
            if (storage == null) continue;

            long extracted = storage.extract(variant, remaining, transaction);
            remaining -= extracted;
            if (extracted > 0) foundTargets.add(Pair.of(target, extracted));

            if (remaining <= 0) break;
        }

        foundTargets.forEach(p ->
        {
//                ItemPipeUtil.stackToAny((ServerWorld) world, p.first().getPos(), Direction.UP, variant, p.right(), transaction);
            ((IServerWorld) world).getItemNetwork().route(p.first().getPos(), p.first().getFace(), fromPos, fromPos.face(), variant, p.right(), transaction);
        });

        return amount - remaining;
    }

    @Override
    public Stream<StorageView<ItemVariant>> getAvailable(TransactionContext transaction)
    {
        return getTargets().stream().map(RetrievalTarget::find).filter(Objects::nonNull).flatMap(s -> Streams.stream(s.iterable(transaction)));
    }
}
