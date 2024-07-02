package com.neep.neepmeat.transport.machine.item;

import com.neep.meatweapons.item.filter.FilterList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class FilteredEjectorBlockEntity extends EjectorBlockEntity
{
    private final FilterList filterList = new FilterList();

    public FilteredEjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        super.serverTick();
    }

    protected void tryTransfer()
    {
        Storage<ItemVariant> storage;
        if ((storage = extractionCache.find()) != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> extractable = StorageUtil.findExtractableContent(storage, filterList::matches, transaction);

                if (extractable == null)
                {
                    transaction.abort();
                    return;
                }

                long extracted = storage.extract(extractable.resource(), 1, transaction);

                if (extracted >= 1)
                {
                    succeed();
                    stored = new ResourceAmount<>(extractable.resource(), extracted);
                    transaction.commit();
                    return;
                }
                transaction.abort();
            }
        }
    }
}
