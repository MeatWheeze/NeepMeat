package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.api.block.BaseFacingBlock;
import com.neep.neepmeat.block.IItemPipe;
import com.neep.neepmeat.block.machine.ItemPumpBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemPumpBlockEntity extends BlockEntity
{
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
        if (world.getTime() % 10 == 0)
        {
            Direction facing = state.get(BaseFacingBlock.FACING);

            Storage<ItemVariant> storage;
            if ((storage = ItemStorage.SIDED.find(world, pos.offset(facing.getOpposite()), facing.getOpposite())) != null)
            {
                Transaction transaction = Transaction.openOuter();
                ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, transaction);
                if (extractable == null)
                {
                    transaction.abort();
                    return;
                }

                long transferred = storage.extract(extractable.resource(), extractable.amount(), transaction);
                long forwarded = be.forwardItem(new ResourceAmount<>(extractable.resource(), transferred));
                if (forwarded < 1)
                {
                    transaction.abort();
                    return;
                }
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
}
