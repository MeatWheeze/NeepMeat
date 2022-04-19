package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.inventory.ImplementedInventory;
import com.neep.neepmeat.screen_handler.BufferScreenHandler;
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
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class BufferBlockEntity extends LootableContainerBlockEntity implements ImplementedInventory, NamedScreenHandlerFactory
{
    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

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
        Inventories.writeNbt(tag, items);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return items;
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected Text getContainerName()
    {
        return null;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList()
    {
        return items;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list)
    {
        this.items = list;
    }

//    @Nullable
//    @Override
//    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
//    {
//        return new BufferScreenHandler(syncId, inv);
//        return new Generic3x3ContainerScreenHandler(syncId, inv);
//    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory inv)
    {
//        return new Generic3x3ContainerScreenHandler(syncId, inv);
        return new BufferScreenHandler(syncId, inv, this);
    }
}
