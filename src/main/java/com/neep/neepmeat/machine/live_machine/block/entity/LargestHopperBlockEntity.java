package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LargestHopperBlockEntity extends SyncableBlockEntity implements LivingMachineComponent
{
    private final ImplementedInventory inventory = ImplementedInventory.ofSize(4, this::markDirty);
    private final InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);

    public LargestHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.LARGEST_HOPPER;
    }

    public void insertEntity(ItemEntity itemEntity)
    {
        ItemStack stack = itemEntity.getStack();
        if (inventory.isEmpty())
        {
            inventory.setStack(0, itemEntity.getStack());
            itemEntity.remove(Entity.RemovalReason.DISCARDED);
        }
        else
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                long inserted = inventoryStorage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                if (inserted == stack.getCount())
                {
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);
                }
                else
                {
                    stack.decrement((int) inserted);
                }
            }
        }
    }

    public InventoryStorage getStorage(Direction unused)
    {
        return inventoryStorage;
    }
}
