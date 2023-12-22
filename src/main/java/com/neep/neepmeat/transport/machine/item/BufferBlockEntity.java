package com.neep.neepmeat.transport.machine.item;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.meatlib.inventory.InventoryImpl;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class BufferBlockEntity extends BlockEntity implements
        Storage<ItemVariant>,
        NamedScreenHandlerFactory
{
    public InventoryImpl inventory = new BufferInventory();

    public BufferBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.BUFFER, pos, state);
    }

    public BufferBlockEntity(BlockEntityType<BufferBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        inventory.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        inventory.readNbt(tag);
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new BufferScreenHandler(syncId, inv, this.inventory);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
        if (getCachedState().get(BufferBlock.POWERED))
        {
            return 0;
        }
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
    {
        InventoryStorage storage = InventoryStorage.of(this.inventory, Direction.UP);
//        if (getCachedState().get(BufferBlock.POWERED))
//        {
//            return Collections.emptyIterator();
//        }
        return (Iterator<StorageView<ItemVariant>>) storage.iterator(transaction);
    }
}
