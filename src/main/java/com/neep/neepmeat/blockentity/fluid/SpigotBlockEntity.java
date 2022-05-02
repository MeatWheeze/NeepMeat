package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.block.FluidPortBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class SpigotBlockEntity extends BlockEntity implements Storage<FluidVariant>
{
    protected BlockApiCache<Storage<FluidVariant>, Direction> cache;

    public SpigotBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public SpigotBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.SPIGOT, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (cache == null)
        {
            updateApiCache(getPos(), getCachedState());
        }
        Storage<FluidVariant> storage = cache.find(getCachedState().get(FluidPortBlock.FACING));
        if (storage != null)
        {
            return storage.insert(resource, maxAmount, transaction);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (cache == null)
        {
            updateApiCache(getPos(), getCachedState());
        }
        Storage<FluidVariant> storage = cache.find(getCachedState().get(FluidPortBlock.FACING));
        if (storage != null)
        {
            return storage.extract(resource, maxAmount, transaction);
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        if (cache == null)
        {
            updateApiCache(getPos(), getCachedState());
        }
        Storage<FluidVariant> storage = cache.find(getCachedState().get(FluidPortBlock.FACING));
        if (storage != null)
        {
            return storage.iterator(transaction);
        }
        return Collections.emptyIterator();
    }

    public void updateApiCache(BlockPos pos, BlockState state)
    {
        if (getWorld() == null || !(getWorld() instanceof ServerWorld))
            return;

        Direction direction = state.get(FluidPortBlock.FACING);
        cache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) getWorld(), pos.offset(direction));
    }

    public boolean hasCache()
    {
        return cache != null;
    }
}
