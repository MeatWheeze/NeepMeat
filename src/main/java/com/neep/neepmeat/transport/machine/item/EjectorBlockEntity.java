package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.transport.block.fluid_transport.PumpBlock;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EjectorBlockEntity extends SyncableBlockEntity
{
    public static final String NBT_COOLDOWN = "cooldown";

    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> insertionCache = LazyBlockApiCache.of(ItemStorage.SIDED,
            pos.offset(getCachedState().get(EjectorBlock.FACING)), this::getWorld,
            () -> getCachedState().get(EjectorBlock.FACING).getOpposite());
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> extractionCache = LazyBlockApiCache.of(ItemStorage.SIDED,
            pos.offset(getCachedState().get(EjectorBlock.FACING).getOpposite()), this::getWorld,
            () -> getCachedState().get(EjectorBlock.FACING));

    public int cooldown;
    @Nullable protected ResourceAmount<ItemVariant> stored;

    public int shuttle;
    public boolean needsRefresh;
    protected boolean activeWithoutRedstone = false;

    // Client only
    public double offset;

    public EjectorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.EJECTOR, pos, state);
    }

    public EjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        this.shuttle = tag.getInt("shuttle_ticks");
    }

    @Override
    public void toClientTag(NbtCompound tag)
    {
        tag.putInt("shuttle_ticks", shuttle);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt(NBT_COOLDOWN, cooldown);
        tag.put("stored", Instruction.writeItem(stored));
        tag.putBoolean("active_without_redstone", activeWithoutRedstone);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt(NBT_COOLDOWN);
        this.stored = Instruction.readItem(nbt.getCompound("stored"));
        this.activeWithoutRedstone = nbt.getBoolean("active_without_redstone");
    }

    public void serverTick()
    {
        cooldown = Math.max(cooldown - 1, 0);

        if (shuttle > 0)
        {
            --shuttle;
            if (shuttle == 0)
                sync();
        }

        eject();

        if (cooldown == 0 && getCachedState().get(EjectorBlock.ACTIVE) && stored == null)
        {
            cooldown = 10;
            transferTick();
        }
    }

    protected void transferTick()
    {
        Storage<ItemVariant> storage;
        if ((storage = extractionCache.find()) != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, transaction);

                if (extractable == null)
                {
                    transaction.abort();
                    return;
                }

                long extracted = storage.extract(extractable.resource(), 1, transaction);


                if (extracted >= 1)
                {
                    succeed();
                    stored = new ResourceAmount<>(extractable.resource(), extracted);
                    transaction.commit();
                    return;
                }
                transaction.abort();
            }
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

    public long forwardToEntity(ResourceAmount<ItemVariant> resource, Transaction transaction)
    {
        BlockPos pos1 = getPos().offset(getCachedState().get(EjectorBlock.FACING));
        Box toBox = Box.of(Vec3d.ofCenter(pos1), 1, 1, 1);
        Vec3d centre = Vec3d.ofCenter(pos1);
        List<StorageMinecartEntity> toMinecarts = world.getEntitiesByType(TypeFilter.instanceOf(StorageMinecartEntity.class), toBox, (entity -> true));
        StorageMinecartEntity minecart = MiscUtil.closestEntity(toMinecarts, centre);
        if (minecart != null)
        {
            Storage<ItemVariant> storage = InventoryStorage.of(minecart, null);
            return storage.insert(resource.resource(), resource.amount(), transaction);
        }
        return 0;
    }

    public void succeed()
    {
        this.shuttle = 3;
        sync();
    }

    public void changeMode()
    {
        activeWithoutRedstone = !activeWithoutRedstone;
        updatePowered(getWorld().isReceivingRedstonePower(pos));
        markDirty();
    }

    public void updatePowered(boolean powered)
    {
        boolean active = powered != activeWithoutRedstone;
        BlockState newState = getCachedState().with(PumpBlock.ACTIVE, active);
        world.setBlockState(pos, newState);
    }
}
