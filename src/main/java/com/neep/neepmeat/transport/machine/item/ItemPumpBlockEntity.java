package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.machine.BloodMachineBlockEntity;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> insertionCache = LazyBlockApiCache.of(ItemStorage.SIDED,
            pos.offset(getCachedState().get(ItemPumpBlock.FACING)),
            this::getWorld,
            () -> getCachedState().get(ItemPumpBlock.FACING).getOpposite());

    @Nullable
    protected ResourceAmount<ItemVariant> stored;

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
            be.updateRetrievalCache((ServerWorld) world, pos, face, be);
        }

        if (be.shuttle > 0)
        {
            --be.shuttle;
            if (be.shuttle == 0)
                be.sync();
        }

        be.eject();

        if (be.cooldown == 0 && be.active && be.stored == null)
        {
            be.cooldown = 10;
            be.transferTick();
        }

    }

    protected void eject()
    {
        if (stored == null)
            return;

        try (Transaction transaction = Transaction.openOuter())
        {
            long forwarded = forwardItem(new ResourceAmount<>(stored.resource(), Math.min(16, stored.amount())), transaction);
            if (forwarded == stored.amount())
            {
                stored = null;
            }
            else
            {
                stored = new ResourceAmount<>(stored.resource(), stored.amount() - forwarded);
            }
            transaction.commit();
        }
    }

    private void transferTick()
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
        return forwardItem(new ItemInPipe(amount, world.getTime()), transaction);
    }

    public long forwardItem(ItemInPipe item, TransactionContext transaction)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);

        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find()) != null)
        {
            Transaction nested = transaction.openNested();
            long transferred = storage.insert(item.resource(), item.amount(), nested);
            nested.commit();
            return transferred;
        }
        return ItemPipeUtil.pipeToAny(item, getPos(), facing, getWorld(), transaction, true);
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
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        Storage<ItemVariant> storage;
        if (insertionCache != null && (storage = insertionCache.find()) != null)
        {
            return MeatlibStorageUtil.simulateInsert(storage, amount.resource(), amount.amount(), transaction);
        }
        return amount.amount();
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
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

        tag.put("stored", Instruction.writeItem(stored));

//        if (stored != null)
//            tag.put("stored", MeatlibStorageUtil.amountToNbt(stored));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.shuttle = nbt.getInt("shuttle_ticks");
        this.active = nbt.getBoolean(NBT_ACTIVE);
        this.cooldown = nbt.getInt(NBT_COOLDOWN);
        this.stored = Instruction.readItem(nbt.getCompound("stored"));
//        if (nbt.contains("stored"))
//            this.stored = MeatlibStorageUtil.amountFromNbt(nbt.getCompound("stored"));
    }

}
