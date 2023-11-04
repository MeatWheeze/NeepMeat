package com.neep.neepmeat.machine.synthesiser;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SynthesiserStorage implements NbtSerialisable
{
    private final SynthesiserBlockEntity parent;

    protected final WritableSingleFluidStorage meatStorage;
    protected final WritableSingleFluidStorage fluidStorage;
    protected final CombinedStorage<FluidVariant, WritableSingleFluidStorage> combined;

    public static Storage<FluidVariant> getFluidStorage(SynthesiserBlockEntity be, Direction direction)
    {
        return direction.getAxis() != Direction.Axis.Y ? be.storage.combined : null;
    }

    public SynthesiserStorage(SynthesiserBlockEntity parent)
    {
        this.parent = parent;
        this.meatStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET * 9, this.parent::sync)
        {
            @Override
            protected boolean canInsert(FluidVariant variant)
            {
                return variant.isOf(NMFluids.STILL_MEAT);
            }
        };

        this.fluidStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET * 9, this.parent::sync)
        {
            @Override
            protected boolean canInsert(FluidVariant variant)
            {
                return super.canInsert(variant);
            }
        };
        combined = new CombinedStorage<>(List.of(meatStorage));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("meatStorage", meatStorage.toNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.meatStorage.readNbt(nbt.getCompound("meatStorage"));
    }
}
