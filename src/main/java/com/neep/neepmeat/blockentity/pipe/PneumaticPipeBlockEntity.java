package com.neep.neepmeat.blockentity.pipe;

import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.awt.font.TransformAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PneumaticPipeBlockEntity extends BlockEntity implements Storage<ItemVariant>, BlockEntityClientSerializable
{
//    protected List<Pair<PipeOffset, ItemStack>> items = new ArrayList<>();
    protected List<ItemInPipe> items = new ArrayList<>();

    public PneumaticPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PNEUMATIC_PIPE, pos, state);
//        items.add(new Pair<>(new PipeOffset(Direction.NORTH, Direction.UP), new ItemStack(Items.COBBLED_DEEPSLATE)));
    }

    public PneumaticPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
//        items.add(new Pair<>(new PipeOffset(Direction.UP, Direction.EAST), resource.toStack(1)));
//        items.add(new ItemInPipe(Direction.UP, Direction.EAST, resource.toStack(1)));
        this.insert(new ItemInPipe(Direction.NORTH, Direction.NORTH, resource.toStack()), getWorld(), getCachedState(), getPos(), Direction.UP);
        System.out.println(items);
        sync();
        return 1;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        return null;
    }

    public List<ItemInPipe> getItems()
    {
        return items;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        NbtList itemList = (NbtList) tag.get("items");
//        System.out.println(PipeOffset.fromNbt((NbtCompound) itemList.get(0)).getItemStack());
        int size = itemList != null ? itemList.size() : 0;
        items.clear();

        for (int i = 0; i < size; ++i)
        {
            items.add(ItemInPipe.fromNbt(itemList.getCompound(i)));
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        NbtList itemList = new NbtList();
        for (ItemInPipe offset : items)
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1 = offset.toNbt(nbt1);
            itemList.add(nbt1);
        }

        tag.put("items", itemList);
        return tag;
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, PneumaticPipeBlockEntity be)
    {
        Iterator<ItemInPipe> it = be.items.listIterator();
        while (it.hasNext())
        {
            ItemInPipe item = it.next();
            item.step(0.01f);
            if (item.progress >= 1)
            {
                be.transfer(it, item, blockPos, blockState, world);
            }
            be.sync();
        }
    }

    public static boolean insert(ItemInPipe item, World world, BlockState state, BlockPos pos, Direction in)
    {
        Storage<ItemVariant> storage;
        if (world.getBlockEntity(pos) instanceof PneumaticPipeBlockEntity be)
        {
            Direction out = Direction.UP;
            for (Direction dir : IItemPipe.getConnections(state))
            {
                out = dir != in ? dir : out;
            }
            item.reset(in, out);
            be.items.add(item);
            return true;
        }
        else if ((storage = ItemStorage.SIDED.find(world, pos, in)) != null)
        {
            Transaction t = Transaction.openOuter();
            storage.insert(ItemVariant.of(item.getItemStack()), 1, t);
            t.commit();
            return true;
        }
        return false;
    }

    public void transfer(Iterator<ItemInPipe> it, ItemInPipe item, BlockPos pos, BlockState state, World world)
    {
        BlockPos pos1 = pos.offset(item.out);
        BlockState state1 = world.getBlockState(pos1);
        Block block = state1.getBlock();

        Storage<ItemVariant> storage;
        if (block instanceof IItemPipe)
        {
            if (IItemPipe.isConnectedIn(world, pos, state, item.out))
            {
                if (insert(item, world, state1, pos1, item.out.getOpposite()))
                    it.remove();
            }
        }
        else if ((storage = ItemStorage.SIDED.find(world, pos1, item.out.getOpposite())) != null)
        {
            Transaction t = Transaction.openOuter();
            long transferred = storage.insert(ItemVariant.of(item.getItemStack()), item.getItemStack().getCount(), t);
            if (transferred > 0)
                it.remove();
            t.commit();
        }
        else if (state1.isAir())
        {
            Entity itemEntity = new ItemEntity(world, pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5, item.getItemStack());
            world.spawnEntity(itemEntity);
            it.remove();
        }
    }
}
