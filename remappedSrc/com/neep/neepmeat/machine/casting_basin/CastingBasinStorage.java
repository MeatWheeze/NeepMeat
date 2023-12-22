package com.neep.neepmeat.machine.casting_basin;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class CastingBasinStorage implements NbtSerialisable, ImplementedRecipe.DummyInventory
{
    protected WritableStackStorage outputStorage;
    protected WritableSingleFluidStorage inputStorage;
    protected CastingBasinBlockEntity parent;
    protected boolean locked;

    public CastingBasinStorage(CastingBasinBlockEntity parent)
    {
        this.parent = parent;
        this.outputStorage = new WritableStackStorage(parent::sync, 1)
        {
            @Override
            public boolean supportsInsertion()
            {
                return false;
            }
        };

        this.inputStorage = new WritableSingleFluidStorage(FluidConstants.INGOT, parent::sync)
        {
            @Override
            protected boolean canInsert(FluidVariant variant)
            {
                return super.canInsert(variant) && !locked;
            }

            @Override
            protected boolean canExtract(FluidVariant variant)
            {
                return super.canExtract(variant) && !locked;
            }
        };
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtCompound inputNbt = new NbtCompound();
        inputStorage.writeNbt1(inputNbt);
        nbt.put("input", inputNbt);

        NbtCompound outputNbt = new NbtCompound();
        outputStorage.writeNbt(outputNbt);
        nbt.put("output", outputNbt);

        nbt.putBoolean("locked", locked);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        inputStorage.readNbt((NbtCompound) nbt.get("input"));
        outputStorage.readNbt((NbtCompound) nbt.get("output"));
        locked = nbt.getBoolean("locked");
    }

    public boolean lock()
    {
        if (!locked)
        {
            locked = true;
            return true;
        }
        return false;
    }

    public boolean unlock()
    {
        if (locked)
        {
            locked = false;
            return true;
        }
        return false;
    }

    public WritableSingleFluidStorage fluid(Direction dir)
    {
        return inputStorage;
    }

    public WritableStackStorage item(Direction dir)
    {
        return dir != Direction.UP ? outputStorage : null;
    }
}
