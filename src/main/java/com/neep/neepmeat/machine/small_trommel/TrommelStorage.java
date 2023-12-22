package com.neep.neepmeat.machine.small_trommel;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class TrommelStorage implements NbtSerialisable
{
    protected WritableSingleFluidStorage fluidInput;
    protected WritableSingleFluidStorage fluidOutput;
    protected WritableStackStorage itemOutput;

    protected SmallTrommelBlockEntity parent;

    public TrommelStorage(SmallTrommelBlockEntity parent)
    {
        this.parent = parent;
        this.fluidInput = new WritableSingleFluidStorage(FluidConstants.BUCKET, parent::markDirty)
        {
            @Override
            protected boolean canInsert(FluidVariant variant)
            {
                return true;
//                return OreFatRegistry.getFromVariant(variant) != null;
            }
        };

        this.fluidOutput = new WritableSingleFluidStorage(FluidConstants.BUCKET, parent::markDirty);
        this.itemOutput = new WritableStackStorage(parent::markDirty, 16);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtCompound inputNbt = new NbtCompound();
        fluidInput.toNbt(inputNbt);
        nbt.put("fluidInput", inputNbt);

        NbtCompound outputNbt = new NbtCompound();
        fluidOutput.toNbt(outputNbt);
        nbt.put("fluidOutput", outputNbt);

        NbtCompound itemOutputNbt = new NbtCompound();
        itemOutput.writeNbt(itemOutputNbt);
        nbt.put("itemOutput", itemOutputNbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.fluidInput.readNbt((NbtCompound) Objects.requireNonNull(nbt.get("fluidInput")));
        this.fluidOutput.readNbt((NbtCompound) Objects.requireNonNull(nbt.get("fluidOutput")));
        this.itemOutput.readNbt((NbtCompound) Objects.requireNonNull(nbt.get("itemOutput")));
    }

    public WritableSingleFluidStorage input()
    {
        return fluidInput;
    }

    public WritableSingleFluidStorage output()
    {
        return fluidOutput;
    }

    public WritableStackStorage itemOutput()
    {
        return itemOutput;
    }
}