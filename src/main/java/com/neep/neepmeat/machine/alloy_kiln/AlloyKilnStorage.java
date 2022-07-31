package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.util.NbtSerialisable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AlloyKilnStorage implements NbtSerialisable
{
    protected SimpleInventory inventory;
    protected InventoryStorage inventoryStorage;
    protected AlloyKilnBlockEntity parent;

    public AlloyKilnStorage(AlloyKilnBlockEntity parent)
    {
        this.inventory = new SimpleInventory(4);
        this.inventoryStorage = InventoryStorage.of(inventory, null);
        this.parent = parent;
    }

    public Storage<ItemVariant> getStorage(Direction direction)
    {
//        Direction facing = parent.getCachedState().get(AlloyKilnBlock.FACING);
//        Direction left = facing.rotateYClockwise();
//        Direction right = facing.rotateYCounterclockwise();
//        Direction back = facing.getOpposite();

        if (!direction.getAxis().isVertical())
        {
            return inventoryStorage.getSlot(0); // Fuel
        }
        else if (direction == Direction.UP)
        {
            // Ingredient inputs
            return new CombinedStorage<>(List.of(inventoryStorage.getSlot(1), inventoryStorage.getSlot(2)));
        }
        else if (direction == Direction.DOWN)
        {
            // Item output
            return inventoryStorage.getSlot(3);
        }
        return null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        nbt.put("inventory", this.inventory.toNbtList());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.inventory.readNbtList((NbtList) nbt.get("inventory"));
    }

    public void dropItems(World world, BlockPos pos)
    {
        ItemScatterer.spawn(world, pos, inventory);
    }
}
