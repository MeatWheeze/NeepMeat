package com.neep.meatlib.blockentity;

import com.neep.meatlib.inventory.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class InventoryBlockEntity extends SyncableBlockEntity implements ImplementedInventory, SidedInventory
{
    protected DefaultedList<ItemStack> inventory;

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, DefaultedList<ItemStack> stacks)
    {
        super(type, pos, state);
        this.inventory = stacks;
    }

    @Override
    public DefaultedList<ItemStack> getItems()
    {
        return inventory;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
    }
}
