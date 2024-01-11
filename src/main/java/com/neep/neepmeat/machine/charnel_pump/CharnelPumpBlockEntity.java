package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.well_head.BlockEntityFinder;
import com.neep.neepmeat.machine.well_head.WellHeadBlockEntity;
import com.neep.neepmeat.transport.block.fluid_transport.DirectionalFluidAcceptor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;

import java.util.Set;

public class CharnelPumpBlockEntity extends SyncableBlockEntity
{
    private LazySupplier<BlockEntityFinder<WellHeadBlockEntity>> wellHeadFinder = LazySupplier.of(() ->
    {
        var finder =  new BlockEntityFinder<>(getWorld(), NMBlockEntities.WELL_HEAD, 20);

        ChunkPos origin = new ChunkPos(getPos());

        finder.add(origin);
        for (int i = origin.x - 1; i <= 1; ++i)
        {
            for (int k = origin.z - 1; k <= 1; ++k)
            {
                finder.add(new ChunkPos(i, k));
            }
        }

        return finder;
    });

    private WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET * 16, this::sync);

    public CharnelPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        wellHeadFinder.get().tick();

        Set<WellHeadBlockEntity> found = wellHeadFinder.get().result();
        long distributeAmount = FluidConstants.BUCKET; // Integer multiple of bucket, will vary based on power input.

        for (var wellhead : found)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                long extracted = fluidStorage.extract(FluidVariant.of(Fluids.WATER), distributeAmount, transaction);
                if (extracted == distributeAmount)
                {
                    wellhead.receiveFluid(distributeAmount, transaction);
                    transaction.commit();
                }
                else
                {
                    transaction.abort();
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        fluidStorage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt);
    }

    public Storage<FluidVariant> getFluidStorage(Direction face)
    {
        return fluidStorage;
    }
}
