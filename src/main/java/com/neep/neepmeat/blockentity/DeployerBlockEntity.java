package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("UnstableApiUsage")
public class DeployerBlockEntity extends BlockEntity implements SingleSlotStorage<ItemVariant>, BlockEntityClientSerializable
{
    protected final WritableStackStorage storage;

    public DeployerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new WritableStackStorage(this);
    }

    public DeployerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.DEPLOYER, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        return nbt;
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
    public boolean isResourceBlank()
    {
        return storage.isResourceBlank();
    }

    @Override
    public ItemVariant getResource()
    {
        return storage.getResource();
    }

    @Override
    public long getAmount()
    {
        return storage.getAmount();
    }

    @Override
    public long getCapacity()
    {
        return storage.getCapacity();
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
//            System.out.println(getResource() +", " + isResourceBlank() + ", stack: " + stack.isEmpty());
        if ((stack.isEmpty() || !getResource().matches(stack)) && !isResourceBlank())
        {
            Transaction transaction = Transaction.openOuter();
            {
                ItemVariant resource = getResource();
                long extracted = extract(getResource(), Long.MAX_VALUE, transaction);
                player.giveItemStack(resource.toStack((int) extracted));
                transaction.commit();
                sync();
                return true;
            }
        }
        else if (isResourceBlank() && !stack.isEmpty())
        {
            Transaction transaction = Transaction.openOuter();
            {
                long inserted = insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                transaction.commit();
                sync();
                return true;
            }
        }

        return false;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
//       System.out.println("reading client");
        storage.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
//        System.out.println("writing server");
        storage.writeNbt(tag);
        return tag;
    }
}
