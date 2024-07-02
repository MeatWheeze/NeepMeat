package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.RetrievalTarget;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemPumpBlockEntity extends EjectorBlockEntity
{
    protected List<RetrievalTarget<ItemVariant>> retrievalCache = new ArrayList<>();

    public ItemPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.needsRefresh = true;
    }

    public ItemPumpBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_PUMP, pos, state);
    }

    @Override
    public void serverTick()
    {
        if (needsRefresh)
        {
            Direction face = getCachedState().get(ItemPumpBlock.FACING).getOpposite();
            updateRetrievalCache((ServerWorld) world, pos, face, this);
        }

        super.serverTick();
    }

    @Override
    protected void tryTransfer()
    {
        BlockState state = getCachedState();
        Direction facing = state.get(BaseFacingBlock.FACING);

        // Try to extract from adjacent storage
        Storage<ItemVariant> storage;
        if ((storage = ItemStorage.SIDED.find(world, pos.offset(facing.getOpposite()), facing)) != null)
        {
            Transaction transaction = Transaction.openOuter();
            ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, transaction);
            if (extractable == null)
            {
                transaction.abort();
                return;
            }

            long transferred = storage.extract(extractable.resource(), 16, transaction);
            if (transferred <= 0)
            {
                transaction.abort();
                return;
            }
            else
            {
                stored = new ResourceAmount<>(extractable.resource(), transferred);
            }

            succeed();
            transaction.commit();
        }

        // Try to retrieve from pipes
        else if (world.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof ItemPipe pipe)
        {
            Transaction transaction = Transaction.openOuter();
            if (retrieve(transaction))
            {
                succeed();
                transaction.commit();
            }
            else
            {
                transaction.abort();
            }
        }
    }

    // Takes items from connected storages
    public boolean retrieve(Transaction transaction)
    {
        Direction facing = getCachedState().get(BaseFacingBlock.FACING);

        boolean success = false;

        Storage<ItemVariant> facingStorage = insertionCache.find();

        for (RetrievalTarget<ItemVariant> target : retrievalCache)
        {
            Storage<ItemVariant> targetStorage = target.find();

            Transaction nested1 = transaction.openNested();

            ResourceAmount<ItemVariant> extractable;
            if (facingStorage != null)
            {
                Transaction nested2 = nested1.openNested();
                extractable = MeatlibStorageUtil.findExtractableContent(targetStorage,
                        (t, itemVariant) -> MeatlibStorageUtil.simulateInsert(facingStorage, itemVariant, Long.MAX_VALUE, t) > 0, nested2);
                nested2.abort();
            }
            else
            {
                extractable = StorageUtil.findExtractableContent(targetStorage, nested1);
            }

            if (extractable == null)
            {
                nested1.abort();
                continue;
            }

            long transferable = canForward(extractable, nested1);
            if (transferable < 1)
            {
                nested1.abort();
                continue;
            }

            // TODO: change max amount
            long extracted = targetStorage.extract(extractable.resource(), Math.min(transferable, 16), nested1);
            extractable = new ResourceAmount<>(extractable.resource(), extracted);
            long forwarded = forwardRetrieval(new ResourceAmount<>(extractable.resource(), extracted), target, transaction);

            if (forwarded < 1)
            {
                nested1.abort();
                continue;
            }
            nested1.commit();
            success = true;
        }
        return success;
    }

    public void markNeedsRefresh()
    {
        this.needsRefresh = true;
    }

    public long forwardRetrieval(ResourceAmount<ItemVariant> amount, RetrievalTarget<ItemVariant> target, TransactionContext transaction)
    {
//        BlockPos newPos = target.getPos().offset(target.getFace());
//        BlockState state = world.getBlockState(newPos);
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        return ((IServerWorld) world).getItemNetwork().route(target.getPos(), target.getFace(), pos, facing, amount.resource(), (int) amount.amount(), transaction);
//        if (state.getBlock() instanceof IItemPipe pipe)
//        {
//            return pipe.insert(world, newPos, state, target.getFace().getOpposite(), new ItemInPipe(amount, world.getTime()), transaction);
//        }
    }

    private void updateRetrievalCache(ServerWorld world, BlockPos pos, Direction face, ItemPumpBlockEntity be)
    {
        retrievalCache = ItemPipeUtil.floodSearch(pos, face, world, pair -> ItemStorage.SIDED.find(world, pair.getLeft(), pair.getRight()) != null, 16);
        needsRefresh = false;
    }

    public long canForward(ResourceAmount<ItemVariant> amount, Transaction transaction)
    {
        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find()) != null)
        {
            return MeatlibStorageUtil.simulateInsert(storage, amount.resource(), amount.amount(), transaction);
        }
        return amount.amount();
    }
}
