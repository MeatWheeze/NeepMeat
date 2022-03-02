package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.ItemDuctBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
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
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemDuctBlockEntity extends LootableContainerBlockEntity implements
        Storage<ItemVariant>,
        SingleSlotStorage<ItemVariant>
{

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long lastTickTime;
    private BlockApiCache<Storage<ItemVariant>, Direction> cache;

    public ItemDuctBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.ITEM_DUCT_BLOCK_ENTITY, pos, state);

    }

    public SnapshotParticipant<ResourceAmount<ItemVariant>> participant = new SnapshotParticipant<ResourceAmount<ItemVariant>>()
    {

        @Override
        protected ResourceAmount<ItemVariant> createSnapshot()
        {
            return new ResourceAmount<>(getResource(), getAmount());
        }

        @Override
        protected void readSnapshot(ResourceAmount<ItemVariant> snapshot)
        {
            setResource(snapshot.resource());
            setAmount(snapshot.amount());
        }
    };

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
    public int size()
    {
        return this.inventory.size();
    }

    @Override
    public ItemStack removeStack(int slot, int amount)
    {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack)
    {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack())
        {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    protected Text getContainerName()
    {
        return new TranslatableText(NeepMeat.NAMESPACE + ":container.item_duct");
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

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list)
    {
        this.inventory = list;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList()
    {
        return this.inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        // TODO: fix
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    private boolean isFull() {
        for (ItemStack itemStack : this.inventory)
        {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount()) continue;
            return false;
        }
        return true;
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

        Item item = resource.toStack().getItem();
        ItemStack presentStack = inventory.get(0);

        if ((getResource().equals(resource)))
        {
            participant.updateSnapshots(transaction);
            int space = presentStack.getMaxCount() - presentStack.getCount();
            int transferred = (int) Math.min(presentStack.getMaxCount(), Math.min(space, maxAmount));
            presentStack.increment(transferred);
            return transferred;
        }
        else if (presentStack.isEmpty())
        {
            participant.updateSnapshots(transaction);
            int transferred = (int) Math.min(item.getMaxCount(), maxAmount);
            inventory.set(0, resource.toStack(transferred));
            return transferred;
        }
        return 0;
    }

//    @Override
//    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
//    {
//        Item item = resource.toStack().getItem();
//        ItemStack presentStack = inventory.get(0);
//
//        if (getResource() == null || getResource().isBlank() || getAmount() <= 0)
//        {
//            this.setAmount(0);
//        }
//
//        long inserted = Math.min(maxAmount, getCapacity() - getAmount());
//
//        if (getResource().equals(resource) && inserted > 0)
//        {
//            this.updateSnapshots(transaction);
//            amount += inserted;
//            return inserted;
//        }
//        return 0;
//    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        if (getResource().equals(resource))
        {
            participant.updateSnapshots(transaction);
            int transferred = (int) Math.min(inventory.get(0).getCount(), maxAmount);
            inventory.get(0).decrement(transferred);
//            System.out.println("pos: " + getPos() + ", extracted: " + transferred);
            return transferred;
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank()
    {
        return inventory.get(0).isEmpty();
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
//        return inventory.stream().map(stack -> ItemVariant.of(stack)).collect(Collectors.toList());
        return SingleViewIterator.create(this, transaction);
    }

    @Override
    public ItemVariant getResource()
    {
        return ItemVariant.of(inventory.get(0));
    }

    @Override
    public long getCapacity()
    {
        return 64;
    }

    public void setAmount(long amount)
    {
//        System.out.println("setamount "+amount);
        inventory.get(0).setCount((int) amount);
    }

    private void setResource(ItemVariant resource)
    {
        inventory.set(0, resource.toStack());
    }

    public long getAmount()
    {
//        System.out.println("getamount "+ inventory.get(0).getCount());
        return inventory.get(0).getCount();
    }

    public List<ItemStack> getInventory()
    {
        return inventory;
    }

}
