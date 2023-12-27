package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.FluidEnegyRegistry;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class LiquidFuelMachine extends SyncableBlockEntity
{
    protected WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(bufferCapacity(), this::sync);

    public LiquidFuelMachine(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public Storage<FluidVariant> getTank(Direction direction)
    {
        return fluidStorage;
    }

    protected long maxPower()
    {
        return 1000;
    }

    protected long bufferCapacity()
    {
        double baseEnergy = FluidEnegyRegistry.getInstance().getOrEmpty(NMFluids.STILL_ETHEREAL_FUEL).baseEnergy();
        return (long) (maxPower() / baseEnergy);
    }

    public long extractEnergy(long abs, TransactionContext transaction)
    {
        FluidVariant variant = fluidStorage.getResource();
        if (!variant.isBlank())
        {
            double baseEnergy = FluidEnegyRegistry.getInstance().getOrEmpty(variant.getFluid()).baseEnergy();
            long extractAmount = (long) (abs / baseEnergy);
            long extracted = fluidStorage.extract(variant, extractAmount, transaction);

            // This is highly wrong
            return (long) (extracted * baseEnergy);
        }
        return 0;
    }
}
