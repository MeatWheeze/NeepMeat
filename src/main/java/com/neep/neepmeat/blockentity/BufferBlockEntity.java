package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.BufferBlock;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.inventory.BufferInventory;
import com.neep.neepmeat.inventory.ImplementedInventory;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class BufferBlockEntity extends BlockEntity implements
        Storage<ItemVariant>,
        NamedScreenHandlerFactory
{
    public ImplementedInventory inventory = new BufferInventory();

    public BufferBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.BUFFER, pos, state);
    }

    public BufferBlockEntity(BlockEntityType<BufferBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        inventory.writeNbt(tag);
        return tag;
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
        return storage.iterator(transaction);
    }
}
