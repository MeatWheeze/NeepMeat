package com.neep.neepmeat.machine.fluid_exciter;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.processing.FluidEnegyRegistry;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FluidExciterBlockEntity extends SyncableBlockEntity
{
    protected boolean needsUpdate = true;
    BlockApiCache<Storage<FluidVariant>, Direction> downCache;

    protected WritableSingleFluidStorage outputStorage = new WritableSingleFluidStorage(8 * FluidConstants.BUCKET, this::markDirty);

    protected SingleVariantStorage<FluidVariant> inputStorage = new WritableSingleFluidStorage(30 * FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(NMFluids.STILL_ETHEREAL_FUEL);
        }

        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction)
        {
            if (downCache == null) updateCache();

            if (!(world instanceof ServerWorld serverWorld && serverWorld.getServer().isOnThread())) return 0;

            Storage<FluidVariant> downStorage = downCache.find(Direction.DOWN);
            if (downStorage != null)
            {
                try (Transaction inner = transaction.openNested())
                {
                    double baseEnergy1 = FluidEnegyRegistry.getInstance().getOrEmpty(insertedVariant.getFluid()).baseEnergy();
                    double baseEnergy2 = FluidEnegyRegistry.getInstance().getOrEmpty(NMFluids.STILL_CHARGED_WORK_FLUID).baseEnergy();

                    // Find amount of work fluid with equivalent energy
                    long downAmount = (long) ((baseEnergy1 * maxAmount) / baseEnergy2);

                    long downExtracted = downStorage.extract(FluidVariant.of(NMFluids.STILL_WORK_FLUID), downAmount, inner);
                    long upInserted = outputStorage.insert(FluidVariant.of(NMFluids.STILL_CHARGED_WORK_FLUID), downExtracted, inner);

                    long newAmount = (long) (downExtracted * baseEnergy2 / baseEnergy1);

                    if (upInserted == downExtracted)
                    {
                        inner.commit();
                        return newAmount;
                    }
                    inner.abort();
                }
            }
            return 0;
        }

        @Override
        protected boolean canExtract(FluidVariant variant)
        {
            return false;
        }

        @Override
        public boolean supportsExtraction()
        {
            return false;
        }
    };

    public FluidExciterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FluidExciterBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.FLUID_EXCITER, pos, state);
    }

    protected void updateCache()
    {
        downCache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, pos.down());
    }

    public WritableSingleFluidStorage getOutputStorage(Direction direction)
    {
        return outputStorage;
    }

    public SingleVariantStorage<FluidVariant> getInputStorage(Direction direction)
    {
        return inputStorage;
    }

    public static final FluidPump TOP_PUMP = FluidPump.of(1, true);

    public static FluidPump getPump(World world, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction)
    {
        return TOP_PUMP;
    }
}
