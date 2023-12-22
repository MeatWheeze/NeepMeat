package com.neep.neepmeat.transport.block.item_transport;

import com.google.common.collect.Streams;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.item_network.StorageBus;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.item_network.RoutingNetworkDFSFinder;
import com.neep.neepmeat.util.MiscUtils;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StorageBusBlock extends ItemPipeBlock implements IItemPipe
{
    public StorageBusBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient())
        {
            world.getBlockEntity(pos, ItemTransport.STORAGE_BUS_BE).ifPresent(be -> be.update((ServerWorld) world, pos));
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (!world.isClient())
        {
            world.getBlockEntity(pos, ItemTransport.STORAGE_BUS_BE).ifPresent(be -> be.update((ServerWorld) world, pos));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof SBBlockEntity be)
        {
            System.out.println(be.getController());
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, ItemTransport.STORAGE_BUS_BE, StorageBusBlock.SBBlockEntity::serverTick, null, world);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.STORAGE_BUS_BE.instantiate(pos, state);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static class SBBlockEntity extends ItemPipeBlockEntity implements StorageBus
    {
        protected BlockApiCache<RoutingNetwork, Void> controller;
        protected final List<RetrievalTarget<ItemVariant>> targets = new ArrayList<>(6);
        protected final List<Pair<BlockPos, Direction>> storageFaces = new ArrayList<>(6);
        protected boolean needsUpdate = false;

        public SBBlockEntity(BlockPos pos, BlockState state)
        {
            this(ItemTransport.STORAGE_BUS_BE, pos, state);
        }

        public SBBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
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
            NbtList posList = new NbtList();
            targets.forEach(t ->
            {
                NbtCompound compound = NbtHelper.fromBlockPos(t.getPos());
                compound.putInt("direction", t.getFace().ordinal());
                posList.add(compound);
            });
            nbt.put("storage_pos", posList);
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            NbtList posList = nbt.getList("storage_pos", NbtType.COMPOUND);
            posList.forEach(c ->
            {
                NbtCompound compound = (NbtCompound) c;
                BlockPos pos = NbtHelper.toBlockPos(compound);
                Direction direction = Direction.values()[compound.getInt("direction")];
                storageFaces.add(Pair.of(pos, direction));
            });
            needsUpdate = true;
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
                if (getWorld() != null && getWorld() instanceof ServerWorld)
                {
                    targets.clear();
                    storageFaces.forEach(p -> targets.add(new RetrievalTarget<>(BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) getWorld(), p.first()), p.second())));
                    needsUpdate = false;
                }
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
                ((IServerWorld) world).getItemNetwork().route(pos, p.first().getFace(), fromPos.pos, fromPos.face, variant, p.right(), transaction);
            });

            return amount - remaining;
        }

        @Override
        public Stream<StorageView<ItemVariant>> getAvailable(TransactionContext transaction)
        {
            return targets.stream().map(RetrievalTarget::find).filter(Objects::nonNull).flatMap(s -> Streams.stream(s.iterable(transaction)));
        }
    }
}