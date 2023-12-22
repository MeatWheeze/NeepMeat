package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.meatlib.util.NbtSerialisable;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AlloyKilnStorage extends SimpleInventory implements NbtSerialisable
{
    public static int FUEL = 0;
    public static int INPUT_1 = 1;
    public static int INPUT_2 = 2;
    public static int OUTPUT = 3;

    protected ImplementedInventory inventory;
    protected InventoryStorage inventoryStorage;
    protected AlloyKilnBlockEntity parent;

    public AlloyKilnStorage(AlloyKilnBlockEntity parent)
    {
        this.inventory = new ImplementedInventory()
        {
            final DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);

            @Override
            public DefaultedList<ItemStack> getItems()
            {
                return items;
            }

            @Override
            public void markDirty()
            {
                ImplementedInventory.super.markDirty();
                AlloyKilnStorage.this.parent.markDirty();
            }
        };
        this.inventoryStorage = InventoryStorage.of(inventory, null);
        this.parent = parent;
    }

    public Storage<ItemVariant> getStorage(Direction direction)
    {
        if (!direction.getAxis().isVertical())
        {
            return inventoryStorage.getSlot(FUEL);
        }
        else if (direction == Direction.UP)
        {
            // Ingredient inputs
            return getInputStorage();
        }
        else if (direction == Direction.DOWN)
        {
            return inventoryStorage.getSlot(OUTPUT);
        }
        return null;
    }

    public int decrementFuel()
    {
        if (!inventory.isEmpty())
        {
            ItemStack stack = inventory.getStack(FUEL);
            Item remainder = stack.getItem().getRecipeRemainder();
            Integer time = FuelRegistry.INSTANCE.get(stack.getItem());

            if (time != null)
            {
                stack.decrement(1);
                if (remainder != null && stack.isEmpty())
                {
                    inventory.setStack(FUEL, new ItemStack(remainder, 1));
                }
                inventory.markDirty();
                return time;
            }
        }
        return -1;
    }

    public SingleSlotStorage<ItemVariant> getSlot(int i)
    {
        return inventoryStorage.getSlot(i);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        Inventories.writeNbt(nbt, inventory.getItems());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        Inventories.readNbt(nbt, inventory.getItems());
    }

    public void dropItems(World world, BlockPos pos)
    {
        ItemScatterer.spawn(world, pos, inventory);
    }

    public Storage<ItemVariant> getOutputStorage()
    {
        return inventoryStorage.getSlot(OUTPUT);
    }

    public Storage<ItemVariant> getInputStorage()
    {
        return new CombinedStorage<>(List.of(inventoryStorage.getSlot(INPUT_1), inventoryStorage.getSlot(INPUT_2)));
    }

    @Override
    public void markDirty()
    {
        parent.markDirty();
        inventory.markDirty();
    }
}