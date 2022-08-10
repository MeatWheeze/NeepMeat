package com.neep.neepmeat.machine.trommel;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.minecraft.nbt.NbtCompound;

public class TrommelStorage implements NbtSerialisable
{
    protected WritableSingleFluidStorage fluidInput;
    protected WritableSingleFluidStorage fluidOutput;
    protected WritableStackStorage itemOutput;

    public TrommelStorage(TrommelBlockEntity parent)
    {

    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {

    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
