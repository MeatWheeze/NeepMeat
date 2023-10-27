package com.neep.neepmeat.machine.dumper;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class DumperBlockEntity extends SyncableBlockEntity
{
    protected WritableStackStorage storage;
    protected long lastDrop;
    protected int interval = 10;
    protected int counter;
    private boolean active = true;

    public DumperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new WritableStackStorage(this::markDirty, 64);
    }

    public DumperBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.DUMPER, pos, state);
    }

    public WritableStackStorage getStorage(@Nullable Direction direction)
    {
        return direction != Direction.DOWN ? storage : null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("counter", counter);
        nbt.putBoolean("active", active);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.counter = nbt.getInt("counter");
        this.active = nbt.getBoolean("active");
    }

    public static void dropItem(ServerWorld world, BlockPos pos, ItemVariant variant)
    {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        ItemEntity item = new ItemEntity(world, x, y, z, variant.toStack(1), 0, -0.1, 0);
        world.spawnEntity(item);
    }

    // Returns true if the item storage below can accept items or if there is no item storage below.
    public static boolean canDropItem(ServerWorld world, BlockPos startPos, ItemVariant resource, long amount, TransactionContext transaction)
    {
        int distance = 4;
        BlockPos pos = startPos;
        for (int i = 0; i < distance; ++i)
        {
            pos = pos.offset(Direction.DOWN);
            if (!world.isAir(pos))
                break;
        }

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, Direction.UP);
        return storage == null || StorageUtil.simulateInsert(storage, resource, amount, transaction) == amount;
    }

    public void tick()
    {

        counter = Math.min(interval, counter + 1);

        if (counter >= interval && active)
        {
            counter = 0;

            lastDrop = world.getTime();
            try (Transaction transaction = Transaction.openOuter())
            {
                long extractAmount = 1;
                ItemVariant resource = storage.getResource();
                if (!resource.isBlank()
                        && canDropItem((ServerWorld) world, pos, resource, extractAmount, transaction)
                        && storage.extract(resource, extractAmount, transaction) == extractAmount)
                {
                    dropItem((ServerWorld) world, pos, resource);
                    transaction.commit();
                }
                else transaction.abort();
            }
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, DumperBlockEntity be)
    {
        be.tick();
    }

    public void updateActive(boolean power)
    {
        this.active = power;
    }
}
