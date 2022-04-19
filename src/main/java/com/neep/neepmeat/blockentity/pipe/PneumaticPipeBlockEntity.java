package com.neep.neepmeat.blockentity.pipe;

import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.blockentity.machine.ItemPumpBlockEntity;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class PneumaticPipeBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    protected List<ItemInPipe> items = new ArrayList<>();

    public PneumaticPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PNEUMATIC_PIPE, pos, state);
    }

    public PneumaticPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public List<ItemInPipe> getItems()
    {
        return items;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
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

        return tag;
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
            item.tick();
            if (item.progress >= 1)
            {
                be.transfer(it, item, blockPos, blockState, world);
            }
        }
        be.sync();
    }

    public static long insert(ItemInPipe item, World world, BlockState state, BlockPos pos, Direction in)
    {
        Storage<ItemVariant> storage;
        if (world.getBlockEntity(pos) instanceof PneumaticPipeBlockEntity be)
        {
            Direction out;
            List<Direction> connections = ((IItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

            Random rand = world.getRandom();
            if (!connections.isEmpty())
            {
                out = connections.get(rand.nextInt(connections.size()));
            }
            else
            {
                out = in;
            }

            item.reset(in, out, world.getTime());
            be.items.add(item);
            return 1;
        }
        else if (world.getBlockEntity(pos) instanceof ItemPumpBlockEntity be)
        {
            if (be.getCachedState().get(ItemPumpBlock.FACING) == in.getOpposite())
                return be.forwardItem(item.getResourceAmount());
        }
        return 0;
    }

    public static void reset(ItemInPipe item, World world, BlockState state)
    {
        Direction out;
        Direction in = item.out;

        List<Direction> connections = ((IItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

        Random rand = world.getRandom();
        if (!connections.isEmpty())
        {
            out = connections.get(rand.nextInt(connections.size()));
        }
        else
        {
            out = in;
        }

        item.reset(in, out, world.getTime());
    }

    public void transfer(Iterator<ItemInPipe> it, ItemInPipe item, BlockPos pos, BlockState state, World world)
    {
        BlockPos pos1 = pos.offset(item.out);
        BlockState state1 = world.getBlockState(pos1);
        Block block = state1.getBlock();

        boolean success = false;
        Storage<ItemVariant> storage;
        if (block instanceof IItemPipe pipe)
        {
            if (IItemPipe.isConnectedIn(world, pos, state, item.out))
//            if (pipe.getConnections(state1, IItemPipe::all).contains(item.out))
            {
                if (insert(item, world, state1, pos1, item.out.getOpposite()) > 0)
                {
                    it.remove();
                    success = true;
                }
            }
        }
        else if ((storage = ItemStorage.SIDED.find(world, pos1, item.out.getOpposite())) != null)
        {
            Transaction t = Transaction.openOuter();
            long transferred = storage.insert(ItemVariant.of(item.getItemStack()), item.getItemStack().getCount(), t);
            if (transferred > 0)
            {
                it.remove();
                success = true;
            }
            t.commit();
        }
        else if (state1.isAir())
        {
            Direction out = item.out;
            double offset = 0.2;
            Entity itemEntity = new ItemEntity(world,
                    pos1.getX() + 0.5 - offset * out.getOffsetX(),
                    pos1.getY() + 0.1 - offset * out.getOffsetY(),
                    pos1.getZ() + 0.5 - offset * out.getOffsetZ(),
                    item.getItemStack(),
                    out.getOffsetX() * item.speed, out.getOffsetY() * item.speed, out.getOffsetZ() * item.speed);
            world.spawnEntity(itemEntity);
            it.remove();
            success = true;
        }
        if (!success)
        {
            reset(item, world, state);
//            insert(item, world, state, pos, item.out);
//            item.reset(item.out, item.in, world.getTime());
        }
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
