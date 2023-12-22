package com.neep.neepmeat.machine.item_mincer;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;

public class ItemMincerStorage implements NbtSerialisable
{
    protected final WritableSingleFluidStorage outputStorage;
    protected final WritableStackStorage inputStorage;

    public ItemMincerStorage(ItemMincerBlockEntity parent)
    {
        outputStorage = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, parent::sync)
        {
            @Override
            public boolean supportsInsertion()
            {
                return false;
            }
        };

        inputStorage = new WritableStackStorage(parent::sync, 64)
        {
            @Override
            protected boolean canInsert(ItemVariant variant)
            {
                FoodComponent food = variant.getItem().getFoodComponent();
                return food != null;
            }
        };
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        var inNbt = new NbtCompound();
        var outNbt = new NbtCompound();
        inputStorage.writeNbt(inNbt);
        outputStorage.writeNbt1(outNbt);
        nbt.put("output", outNbt);
        nbt.put("input", inNbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        outputStorage.readNbt(nbt.getCompound("output"));
        inputStorage.readNbt(nbt.getCompound("input"));
    }
}
