package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class MixerStorage extends SimpleInventory
{
    protected MixerBlockEntity parent;

    protected WritableSingleFluidStorage fluidInput1;
    protected WritableSingleFluidStorage fluidInput2;
    protected WritableSingleFluidStorage fluidOutput;
    protected WritableStackStorage itemInput;

    public MixerStorage(MixerBlockEntity parent)
    {
        this.parent = parent;
        Runnable callback = parent::sync;
        fluidInput1 = new WritableSingleFluidStorage(FluidConstants.BUCKET, callback);
        fluidInput2 = new WritableSingleFluidStorage(FluidConstants.BUCKET, callback);
        itemInput = new WritableStackStorage(parent::sync);
        fluidOutput = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, callback);
    }

    public List<StorageView<?>> getInputStorages(TransactionContext transaction)
    {
        List<Storage<FluidVariant>> storages = parent.getAdjacentStorages();
        Storage<ItemVariant> itemStorage = parent.getItemStorage(null);
        return Stream.concat(storages.stream(), Stream.of(itemStorage))
                .flatMap(storage -> StreamSupport.stream(storage.iterable(transaction).spliterator(), false)).collect(Collectors.toList());
    }

    public void writeNbt(NbtCompound nbt)
    {
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

    }

    public void readNbt(NbtCompound nbt)
    {
        NbtCompound input1 = nbt.getCompound("input_1");
        fluidInput1.readNbt(input1);

        NbtCompound input2 = nbt.getCompound("input_2");
        fluidInput2.readNbt(input2);

        NbtCompound itemNbt = nbt.getCompound("item_input");
        itemInput.readNbt(itemNbt);

        NbtCompound output = nbt.getCompound("output");
        fluidOutput.readNbt(output);
    }

    public Storage<ItemVariant> getItemInput()
    {
        return itemInput;
    }

    public Storage<FluidVariant> getFluidOutput()
    {
        return fluidOutput;
    }
}