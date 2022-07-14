package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.fluid_transfer.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class MixerStorage extends SimpleInventory
{
    protected MixerBlockEntity parent;

    protected WritableSingleFluidStorage fluidInput1 = new WritableSingleFluidStorage(FluidConstants.BUCKET);
    protected WritableSingleFluidStorage fluidInput2 = new WritableSingleFluidStorage(FluidConstants.BUCKET);

    public MixerStorage(MixerBlockEntity parent)
    {
        this.parent = parent;
    }

    public List<StorageView<FluidVariant>> getFluidInputs(TransactionContext transaction)
    {
        List<Storage<FluidVariant>> storages = parent.getAdjacentStorages();
        return storages.stream()
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
    }

    public void readNbt(NbtCompound nbt)
    {
        NbtCompound input1 = nbt.getCompound("input_1");
        fluidInput1.readNbt(input1);

        NbtCompound input2 = nbt.getCompound("input_2");
        fluidInput2.readNbt(input2);
    }

    public Storage<FluidVariant> getFluidOutput()
    {
        return parent.getOutputStorage();
    }
}