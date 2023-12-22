package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.api.block.BaseFacingBlock;
import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.util.MiscUitls;
import com.neep.neepmeat.util.RetrievalTarget;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ItemPumpBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    public int shuttle;
    public boolean needsRefresh;

    // Client only
    public double offset;

    List<RetrievalTarget<ItemVariant>> retrievalCache = new ArrayList<>();

    public ItemPumpBlockEntity(BlockEntityType<ItemPumpBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.needsRefresh = true;
    }

    public ItemPumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.ITEM_PUMP, pos, state);
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, ItemPumpBlockEntity be)
    {
        if (be.shuttle > 0)
        {
            --be.shuttle;
            be.sync();
        }

        if (world.getTime() % 10 == 0)
        {
            Direction facing = state.get(BaseFacingBlock.FACING);

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
                long forwarded = be.forwardItem(new ResourceAmount<>(extractable.resource(), transferred));
                if (forwarded < 1)
                {
                    transaction.abort();
                    return;
                }
                be.shuttle = 3;
                be.sync();
                transaction.commit();
            }
            else if (world.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof IItemPipe pipe)
            {
//                if (be.needsRefresh)
//                {
                    Direction face = facing.getOpposite();
                    updateRetrievalCache(world, pos, face, be);
//                }
//                else
                {
                    for (RetrievalTarget<ItemVariant> target : be.retrievalCache)
                    {
                        Storage<ItemVariant> storage1 = target.find();

                        Transaction transaction = Transaction.openOuter();
                        ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage1, transaction);

                        if (extractable == null)
                        {
                            transaction.abort();
                            continue;
                        }

                        long transferred = storage1.extract(extractable.resource(), 16, transaction);
                        long forwarded = be.forwardRetrieval(new ResourceAmount<>(extractable.resource(), transferred), target);

                        if (forwarded < 1)
                        {
                            transaction.abort();
                            continue;
                        }
                        be.shuttle = 3;
                        be.sync();
                        transaction.commit();
                    }
                }
            }
        }
    }

    public long forwardItem(ResourceAmount<ItemVariant> amount)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        BlockPos newPos = pos.offset(facing);
        BlockState state = world.getBlockState(newPos);
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            return pipe.insert(world, newPos, state, facing.getOpposite(), amount);
        }
        return 0;
    }

    public long forwardRetrieval(ResourceAmount<ItemVariant> amount, RetrievalTarget<ItemVariant> target)
    {
        System.out.println(target.getFace());
        BlockPos newPos = target.getPos().offset(target.getFace());
        BlockState state = world.getBlockState(newPos);
        if (state.getBlock() instanceof IItemPipe pipe)
        {
            return pipe.insert(world, newPos, state, target.getFace().getOpposite(), amount);
        }

        return 0;
    }

    public static void updateRetrievalCache(World world, BlockPos pos, Direction face, ItemPumpBlockEntity be)
    {
        be.retrievalCache = MiscUitls.floodSearch(pos, face, world, pair -> ItemStorage.SIDED.find(world, pair.getLeft(), pair.getRight()) != null, 16);
        be.needsRefresh = false;
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

}
