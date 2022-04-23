package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.api.block.BaseFacingBlock;
import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.util.MiscUitls;
import com.neep.neepmeat.util.RetrievalTarget;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EjectorBlockEntity extends ItemPumpBlockEntity implements BlockEntityClientSerializable
{
    protected List<RetrievalTarget<ItemVariant>> retrievalCache = new ArrayList<>();
    protected BlockApiCache<Storage<ItemVariant>, Direction> insertionCache;
    protected List<ResourceAmount<ItemVariant>> extractionQueue = new ArrayList<>();

    public EjectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.EJECTOR, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, EjectorBlockEntity be)
    {
        if (!be.active)
            be.cooldown = Math.max(be.cooldown - 1, 0);

        if (be.needsRefresh)
        {
            Direction face = state.get(ItemPumpBlock.FACING).getOpposite();
            updateRetrievalCache((ServerWorld) world, pos, face, be);
        }

        if (be.shuttle > 0)
        {
            --be.shuttle;
            be.sync();
        }

        if (be.cooldown == 0 && be.active)
        {
            be.cooldown = 2;
            Direction facing = state.get(BaseFacingBlock.FACING);

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

                long transferred = storage.extract(extractable.resource(), 1, transaction);
                long forwarded = be.forwardItem(new ResourceAmount<>(extractable.resource(), transferred), transaction);
                if (forwarded < 1)
                {
                    transaction.abort();
                    return;
                }
                be.shuttle = 3;
                be.sync();
                transaction.commit();
            }
        }
    }

    public void markNeedsRefresh()
    {
        this.needsRefresh = true;
    }

    public void updateRedstone(boolean redstone)
    {
        this.active = redstone;
    }

    public static void updateRetrievalCache(ServerWorld world, BlockPos pos, Direction face, EjectorBlockEntity be)
    {
        be.retrievalCache = MiscUitls.floodSearch(pos, face, world, pair -> ItemStorage.SIDED.find(world, pair.getLeft(), pair.getRight()) != null, 16);
        be.insertionCache = BlockApiCache.create(ItemStorage.SIDED, world, pos.offset(face.getOpposite()));
        be.needsRefresh = false;
    }

    public long canForward(ResourceAmount<ItemVariant> amount, Transaction transaction)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find(facing)) != null)
        {
            return storage.simulateInsert(amount.resource(), amount.amount(), transaction);
        }
        return amount.amount();
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        super.fromClientTag(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        super.toClientTag(tag);
        return tag;
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

}
