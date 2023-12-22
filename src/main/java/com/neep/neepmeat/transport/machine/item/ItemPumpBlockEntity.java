package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.util.TubeUtils;
import com.neep.neepmeat.util.ItemInPipe;
import com.neep.neepmeat.util.MiscUtils;
import com.neep.neepmeat.util.RetrievalTarget;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemPumpBlockEntity extends BloodMachineBlockEntity
{
    public static final String NBT_ACTIVE = "active";
    public static final String NBT_COOLDOWN = "cooldown";

    public int cooldown;
    public boolean active;

    public int shuttle;
    public boolean needsRefresh;

    // Client only
    public double offset;

    protected List<RetrievalTarget<ItemVariant>> retrievalCache = new ArrayList<>();
    protected BlockApiCache<Storage<ItemVariant>, Direction> insertionCache;
    protected List<ResourceAmount<ItemVariant>> extractionQueue = new ArrayList<>();

    public static final long USE_AMOUNT = FluidConstants.BUCKET / 150;

    public ItemPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.needsRefresh = true;
    }

    public ItemPumpBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_PUMP, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ItemPumpBlockEntity be)
    {
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
            be.cooldown = 10;
            be.transferTick();
        }
    }

    public boolean transferTick()
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
                return false;
            }

            long transferred = storage.extract(extractable.resource(), 16, transaction);
            long forwarded = forwardItem(new ResourceAmount<>(extractable.resource(), transferred), transaction);
            if (forwarded != -1)
            {
                transaction.abort();
                return false;
            }
            succeed();
            transaction.commit();
        }
        // Try to retrieve from pipes
        else if (world.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof IItemPipe pipe)
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
        return true;
    }

    public boolean retrieve(Transaction transaction)
    {

//        if (doWork(USE_AMOUNT, transaction) == 0)
//        {
//            return false;
//        }

        Direction facing = getCachedState().get(BaseFacingBlock.FACING);

        boolean success = false;
        for (RetrievalTarget<ItemVariant> target : retrievalCache)
        {
            Storage<ItemVariant> targetStorage = target.find();

            Transaction nested1 = transaction.openNested();

            ResourceAmount<ItemVariant> extractable;
            Storage<ItemVariant> facingStorage = insertionCache.find(facing);
            if (facingStorage != null)
            {
                Transaction nested2 = nested1.openNested();
                extractable = StorageUtil.findExtractableContent(targetStorage,
                        itemVariant -> facingStorage.insert(itemVariant, Long.MAX_VALUE, nested2) > 0, nested2);
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
            long forwarded = forwardRetrieval(new ResourceAmount<>(extractable.resource(), extracted), target);

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

    public void succeed()
    {
        this.shuttle = 3;
        sync();
    }

    public void markNeedsRefresh()
    {
        this.needsRefresh = true;
    }

    public void updateRedstone(boolean redstone)
    {
        this.active = redstone;
    }

    public long forwardItem(ResourceAmount<ItemVariant> amount, TransactionContext transaction)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        BlockPos newPos = pos.offset(facing);
        BlockState state = world.getBlockState(newPos);

        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find(facing)) != null)
        {
            Transaction nested = transaction.openNested();
            long transferred = storage.insert(amount.resource(), amount.amount(), nested);
            nested.commit();
            return transferred;
        }
        return TubeUtils.tryTransfer(new ItemInPipe(amount, world.getTime()), getPos(), getCachedState(), facing, getWorld());
//        if (state.getBlock() instanceof IItemPipe pipe)
//        {
//            return pipe.insert(world, newPos, state, facing.getOpposite(), new ItemInPipe(amount, world.getTime()));
//        }
    }

    public long forwardRetrieval(ResourceAmount<ItemVariant> amount, RetrievalTarget<ItemVariant> target)
    {
        BlockPos newPos = target.getPos().offset(target.getFace());
        BlockState state = world.getBlockState(newPos);
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            return pipe.insert(world, newPos, state, target.getFace().getOpposite(), new ItemInPipe(amount, world.getTime()));
        }
        return 0;
    }

    public static void updateRetrievalCache(ServerWorld world, BlockPos pos, Direction face, ItemPumpBlockEntity be)
    {
        be.retrievalCache = MiscUtils.floodSearch(pos, face, world, pair -> ItemStorage.SIDED.find(world, pair.getLeft(), pair.getRight()) != null, 16);
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
        shuttle = tag.getInt("shuttle_ticks");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        tag.putInt("shuttle_ticks", shuttle);
        return tag;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putBoolean(NBT_ACTIVE, active);
        tag.putInt(NBT_COOLDOWN, cooldown);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        this.active = tag.getBoolean(NBT_ACTIVE);
        this.cooldown = tag.getInt(NBT_COOLDOWN);
    }

}
