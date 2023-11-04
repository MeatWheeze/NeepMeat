package com.neep.neepmeat.machine.bottler;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("UnstableApiUsage")
public class BottlerStorage implements NbtSerialisable
{
    private final BottlerBlockEntity parent;
    private final WritableStackStorage itemStorage;

    private Storage<FluidVariant> fillingStorage = null;

    public BottlerStorage(BottlerBlockEntity parent)
    {
        this.parent = parent;
        itemStorage = new WritableStackStorage(parent::sync, 1)
        {
            @Override
            protected boolean canInsert(ItemVariant variant)
            {
                return super.canInsert(variant);
            }

            @Override
            protected void onFinalCommit()
            {
                updateFillingStorage();
                super.onFinalCommit();
            }
        };
    }

    public void updateFillingStorage()
    {
        fillingStorage = FluidStorage.ITEM.find(itemStorage.getAsStack(), ContainerItemContext.ofSingleSlot(itemStorage));
        if (fillingStorage == null) fillingStorage = Storage.empty();
    }

    public WritableStackStorage getItemStorage()
    {
        return itemStorage;
    }

    public Storage<FluidVariant> getFluidStorage()
    {
        if (fillingStorage == null) updateFillingStorage();

        return fillingStorage;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("itemStorage", itemStorage.toNbt(new NbtCompound()));
//        nbt.put("fluidStorage", fluidStorage.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        itemStorage.readNbt(nbt.getCompound("itemStorage"));
//        fluidStorage.readNbt(nbt.getCompound("fluidStorage"));
    }
}
