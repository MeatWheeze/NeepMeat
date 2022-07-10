package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.inventory.BufferInventory;
import com.neep.neepmeat.inventory.GeneralInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class ConverterBaseBlockEntity extends BlockEntity implements Storage<ItemVariant>
{
    protected GeneralInventory inventory;

    public ConverterBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.inventory = new GeneralInventory(3);
    }

    public ConverterBaseBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.CONVERTER_BASE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
    }

    @Override
    public boolean supportsExtraction()
    {
        return false;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        Storage<ItemVariant> inventoryStorage = InventoryStorage.of(inventory, null);
        return inventoryStorage.extract(resource, maxAmount, transaction);
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
}
