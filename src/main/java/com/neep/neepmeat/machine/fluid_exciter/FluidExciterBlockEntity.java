package com.neep.neepmeat.machine.fluid_exciter;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.FluidEnegyRegistry;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.transport.api.BlockEntityUnloadListener;
import com.neep.neepmeat.transport.api.pipe.AbstractBloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.RememberMyNetwork;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FluidExciterBlockEntity extends SyncableBlockEntity implements BlockEntityUnloadListener, RememberMyNetwork
{
    protected long output;

    protected BlockApiCache<Storage<FluidVariant>, Direction> downCache;

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

            double baseEnergy1 = FluidEnegyRegistry.getInstance().getOrEmpty(insertedVariant.getFluid()).baseEnergy() * 1.5;
            output = (long) Math.ceil(baseEnergy1 * maxAmount);

            return maxAmount;
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

    private final BloodAcceptor bloodAcceptor = new AbstractBloodAcceptor()
    {
        @Override
        public long getOutput()
        {
            // Reset output so that it drops to zero when fluid input stops.
            long prevOutput = output;
            output = 0;
            return prevOutput;
        }

        @Override
        public Mode getMode()
        {
            return Mode.SOURCE;
        }
    };

    private final FluidExciterConduitEntity conduitEntity;

    public FluidExciterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        conduitEntity = new FluidExciterConduitEntity(this.pos);
    }

    protected void updateCache()
    {
        downCache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, pos.down());
    }

    public SingleVariantStorage<FluidVariant> getInputStorage(Direction direction)
    {
        return inputStorage;
    }

    public static BloodAcceptor getBloodAcceptorFromTop(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Direction direction)
    {
        if (world.getBlockEntity(pos.down()) instanceof FluidExciterBlockEntity be)
        {
            return be.bloodAcceptor;
        }
        return null;
    }

    public FluidExciterConduitEntity getConduitEntity(Void unused)
    {
        return conduitEntity;
    }

    @Override
    public void markRemoved()
    {
        conduitEntity.onRemove();
        super.markRemoved();
    }

    @Override
    public void onUnload(WorldChunk chunk)
    {
        conduitEntity.onUnload(chunk);
    }

    @Override
    public VascularConduitEntity get()
    {
        return conduitEntity;
    }
}
