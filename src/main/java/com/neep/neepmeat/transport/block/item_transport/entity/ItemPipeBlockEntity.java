package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemPipeBlockEntity extends SyncableBlockEntity
{
    protected List<ItemInPipe> items = new ArrayList<>();

    public ItemPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.PNEUMATIC_PIPE, pos, state);
    }

    public ItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public List<ItemInPipe> getItems()
    {
        return items;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);

        NbtList itemList = new NbtList();
        for (ItemInPipe offset : items)
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1 = offset.toNbt(nbt1);
            itemList.add(nbt1);
        }

        tag.put("items", itemList);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);

        NbtList itemList = (NbtList) tag.get("items");
        int size = itemList != null ? itemList.size() : 0;
        items.clear();
        for (int i = 0; i < size; ++i)
        {
            items.add(ItemInPipe.fromNbt(itemList.getCompound(i)));
        }
    }
    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, ItemPipeBlockEntity be)
    {
        if (be.items.isEmpty())
            return;

        Iterator<ItemInPipe> it = be.items.listIterator();
        while (it.hasNext())
        {
            ItemInPipe item = it.next();
            item.tick();
            if (item.progress >= 1)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    long transferred = ItemPipeUtil.pipeToAny(item, blockPos, item.out, world, transaction, false);
                    if (transferred == item.getAmount() || item.getItemStack().isEmpty())
                    {
                        it.remove();
                    } else
                    {
                        ItemPipeUtil.bounce(item, world, blockState);
                    }
                    transaction.commit();
                }
            }
        }
        be.sync();
    }

    public long insert(ItemInPipe item, World world, BlockState state, BlockPos pos, Direction in, TransactionContext transaction)
    {
        transaction.addOuterCloseCallback(r ->
        {
            if (!r.wasCommitted()) return;

            Direction out = ((IItemPipe) getCachedState().getBlock()).getOutputDirection(item, state, world, in);
            item.reset(in, out, world.getTime());
            this.items.add(item);
        });
        return item.getItemStack().getCount();
    }

    public void dropItems()
    {
        for (ItemInPipe item : items)
        {
            Entity itemEntity = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, item.getItemStack());
            getWorld().spawnEntity(itemEntity);
        }
        items.clear();
    }
}