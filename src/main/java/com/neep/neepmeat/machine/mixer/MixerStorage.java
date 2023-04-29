package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MixerStorage implements ImplementedRecipe.DummyInventory
{
    protected MixerBlockEntity parent;

    protected WritableSingleFluidStorage fluidInput1;
    protected WritableSingleFluidStorage fluidInput2;
    protected WritableSingleFluidStorage fluidOutput;
    protected WritableStackStorage itemInput;

    // Silly method of remembering the input fluids to show their particles
    public FluidVariant displayInput1 = FluidVariant.blank();
    public FluidVariant displayInput2 = FluidVariant.blank();

    public MixerStorage(MixerBlockEntity parent)
    {
        this.parent = parent;
        Runnable callback = parent::sync;
        fluidInput1 = new WritableSingleFluidStorage(FluidConstants.BUCKET, callback);
        fluidInput2 = new WritableSingleFluidStorage(FluidConstants.BUCKET, callback);
        itemInput = new WritableStackStorage(parent::sync);
        fluidOutput = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, callback);
    }

    public Storage<FluidVariant> getInputStorages()
    {
        List<Storage<FluidVariant>> storages = parent.getAdjacentStorages();
        return new CombinedStorage<>(storages);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("displayInput1", displayInput1.toNbt());
        nbt.put("displayInput2", displayInput2.toNbt());
        
        NbtCompound input1 = new NbtCompound();
        fluidInput1.writeNbt(input1);
        nbt.put("input_1", input1);

        NbtCompound input2 = new NbtCompound();
        fluidInput2.writeNbt(input2);
        nbt.put("input_2", input2);

        NbtCompound itemNbt = new NbtCompound();
        itemInput.writeNbt(itemNbt);
        nbt.put("item_input", itemNbt);

        NbtCompound output = new NbtCompound();
        fluidOutput.writeNbt(output);
        nbt.put("output", output);

        return nbt;
    }

    public void readNbt(NbtCompound nbt)
    {
        this.displayInput1 = FluidVariant.fromNbt(nbt.getCompound("displayInput1"));
        this.displayInput2 = FluidVariant.fromNbt(nbt.getCompound("displayInput2"));
        
        NbtCompound input1 = nbt.getCompound("input_1");
        fluidInput1.readNbt(input1);

        NbtCompound input2 = nbt.getCompound("input_2");
        fluidInput2.readNbt(input2);

        NbtCompound itemNbt = nbt.getCompound("item_input");
        itemInput.readNbt(itemNbt);

        NbtCompound output = nbt.getCompound("output");
        fluidOutput.readNbt(output);
    }

    public WritableStackStorage getItemInput()
    {
        return itemInput;
    }

    public Storage<FluidVariant> getFluidOutput()
    {
        return fluidOutput;
    }
}