package com.neep.neepmeat.machine.casting_basin;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class CastingBasinStorage implements NbtSerialisable, ImplementedRecipe.DummyInventory
{
    protected WritableStackStorage outputStorage;
    protected WritableSingleFluidStorage inputStorage;
    protected CastingBasinBlockEntity parent;

    public CastingBasinStorage(CastingBasinBlockEntity parent)
    {
        this.parent = parent;
        this.outputStorage = new WritableStackStorage(parent::sync, 1);
        this.inputStorage = new WritableSingleFluidStorage(FluidConstants.INGOT, parent::sync);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        NbtCompound inputNbt = new NbtCompound();
        inputStorage.writeNbt(inputNbt);
        nbt.put("input", inputNbt);

        NbtCompound outputNbt = new NbtCompound();
        outputStorage.writeNbt(outputNbt);
        nbt.put("output", outputNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        inputStorage.readNbt((NbtCompound) nbt.get("input"));
        outputStorage.readNbt((NbtCompound) nbt.get("output"));
    }

    public Storage<FluidVariant> fluid(Direction dir)
    {
        return inputStorage;
    }

    public Storage<ItemVariant> item(Direction dir)
    {
        return dir != Direction.UP ? outputStorage : null;
    }
}
