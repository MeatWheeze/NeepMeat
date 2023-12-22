package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.item_transport.ItemDuctBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class ItemDuctBlockEntity extends BlockEntity implements Storage<ItemVariant>
{

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private BlockApiCache<Storage<ItemVariant>, Direction> cache;

    protected WritableStackStorage storage;
    private long lastTickTime;

    public ItemDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new WritableStackStorage(this);
    }

    public ItemDuctBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_DUCT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        storage.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        storage.readNbt(tag);
    }

    public void updateApiCache(BlockPos pos, BlockState state)
    {
        if (getWorld() == null || !(getWorld() instanceof ServerWorld))
            return;

        Direction direction = state.get(ItemDuctBlock.FACING);
        cache = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) getWorld(), pos.offset(direction));
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ItemDuctBlockEntity blockEntity)
    {
        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getTime();
        if (!blockEntity.needsCooldown())
        {
            blockEntity.setCooldown(8);
            insertTick(world, pos, state, blockEntity);
        }
    }

    private static boolean insertTick(World world, BlockPos pos, BlockState state, ItemDuctBlockEntity be)
    {
        if (be.getResource().isBlank())
            return false;

        Direction targetDirection = state.get(ItemDuctBlock.FACING);
        if (be.cache == null)
        {
            be.updateApiCache(pos, state);
        }

        Storage<ItemVariant> storage = be.cache.find(targetDirection);

        // Spawn item entities at open ends
        if (storage == null)
        {
            if (world.getBlockState(pos.offset(targetDirection)).isAir())
            {
                Transaction transaction = Transaction.openOuter();

                BlockPos pos2 = pos.offset(targetDirection);
                Vec3d pos3 = new Vec3d(pos2.getX() + 0.5,
                        pos2.getY() + (targetDirection.getAxis().isHorizontal() ? 0.1 : 0.5),
                        pos2.getZ() + 0.5);
                ItemEntity item = new ItemEntity(world, pos3.getX(), pos3.getY(), pos3.getZ(), be.getResource().toStack((int) be.getAmount()),
                        0, 0, 0);
                be.extract(be.getResource(), be.getAmount(), transaction);
                world.spawnEntity(item);

                transaction.commit();
                return true;
            }
            return false;
        }

        Transaction transaction = Transaction.openOuter();

        long transferAmount = 0;
        if (storage.supportsInsertion() && be.supportsExtraction() && !be.getResource().isBlank())
        {
            transferAmount = StorageUtil.move(be, storage, type -> true, 1, transaction);
        }

        transaction.commit();
        return transferAmount > 0;
    }

    private void setCooldown(int cooldown)
    {
        this.transferCooldown = cooldown;
    }

    private boolean needsCooldown()
    {
        return this.transferCooldown > 0;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        return storage.iterator(transaction);
    }

    public ItemVariant getResource()
    {
        return storage.getResource();
    }

    public long getAmount()
    {
        return storage.getAmount();
    }
}
