package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.api.block.BaseFacingBlock;
import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemPumpBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    public int shuttle;

    // Client only
    public double offset;

    public ItemPumpBlockEntity(BlockEntityType<ItemPumpBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
        }
    }

    public long forwardItem(ResourceAmount<ItemVariant> variant)
    {
        Direction facing = getCachedState().get(ItemPumpBlock.FACING);
        BlockPos newPos = pos.offset(facing);
        BlockState state = world.getBlockState(newPos);
        if (state.getBlock() instanceof IItemPipe pipe)
        {
//            System.out.println("ooer");
            return pipe.insert(world, newPos, state, facing.getOpposite(), variant);
        }
        return 0;
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
