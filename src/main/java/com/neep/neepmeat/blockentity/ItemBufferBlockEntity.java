package com.neep.neepmeat.blockentity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ItemBufferBlockEntity extends SyncableBlockEntity
{
    protected final WritableStackStorage storage;

    public float stackRenderDelta; // Used by the renderer

    public ItemBufferBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new WritableStackStorage(this);
    }

    public ItemBufferBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, pos, state);
    }

    public WritableStackStorage getStorage(@Nullable Direction direction)
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        this.storage.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        this.storage.readNbt(tag);
    }

    public void extractFromItem(ItemEntity itemEntity)
    {
        ItemStack itemStack = itemEntity.getStack();

        Transaction transaction = Transaction.openOuter();

        int transferred = (int) storage.insert(ItemVariant.of(itemStack), itemStack.getCount(), transaction);
        itemStack.decrement(transferred);
        if (itemStack.getCount() <= 0)
        {
            itemEntity.discard();
        }

        transaction.commit();

    }
}
