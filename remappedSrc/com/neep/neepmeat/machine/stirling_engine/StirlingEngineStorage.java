package com.neep.neepmeat.machine.stirling_engine;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineStorage
{
    protected SimpleInventory inventory;

    public StirlingEngineStorage(StirlingEngineBlockEntity parent)
    {
        this.inventory = new SimpleInventory(1);
    }

    public Storage<ItemVariant> getFuelStorage(Direction direction)
    {
        return InventoryStorage.of(inventory, direction);
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    public int decrementFuel()
    {
        if (!inventory.isEmpty())
        {
            ItemStack stack = inventory.getStack(0);
            Item remainder = stack.getItem().getRecipeRemainder();
            Integer time = FuelRegistry.INSTANCE.get(stack.getItem());

            if (time != null)
            {
                stack.decrement(1);
                if (remainder != null && stack.isEmpty())
                {
                    inventory.addStack(new ItemStack(remainder, 1));
                }
                return time;
            }
        }

        return -1;
    }

    public void dropItems(World world, BlockPos pos)
    {
        ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStack(0));
    }

    public void writeNbt(NbtCompound nbt)
    {
        nbt.put("inventory", this.inventory.toNbtList());
    }

    public void readNbt(NbtCompound nbt)
    {
        this.inventory.readNbtList((NbtList) nbt.get("inventory"));
    }
}
