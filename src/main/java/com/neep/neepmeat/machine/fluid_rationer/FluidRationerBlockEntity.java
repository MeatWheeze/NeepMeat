package com.neep.neepmeat.machine.fluid_rationer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.MeatStorageUtil;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class FluidRationerBlockEntity extends SyncableBlockEntity
{
    protected final WritableSingleFluidStorage outputStorage;
    protected final FluidPump outPump = FluidPump.of(0.1f, this::getOutMode, true);

    protected AcceptorModes inMode = AcceptorModes.PULL;
    protected AcceptorModes outMode = AcceptorModes.NONE;

    protected State state;

    protected BlockApiCache<Storage<FluidVariant>, Direction> cache;

    protected long targetAmount = 81000;

    public FluidRationerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.outputStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET * 4, this::markDirty);
        this.state = State.IDLE;
    }

    public FluidRationerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLUID_RATIONER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FluidRationerBlockEntity be)
    {
        be.tickInput();
    }

    protected void tickInput()
    {
        if (cache == null) updateCache();

        if (cache == null) return;

        if (state == State.IDLE)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                Direction back = getCachedState().get(FluidRationerBlock.FACING).getOpposite();
                Storage<FluidVariant> storage = cache.find(back);

                // Find a fluid in the connected tank with an amount that matches the target
                FluidVariant variant = MeatStorageUtil.findExtractableResource(storage, (t, v, a) -> a >= targetAmount, transaction);
                if (variant != null)
                {
                    if (StorageUtil.move(storage, outputStorage, v -> true, targetAmount, transaction) == targetAmount)
                    {
                        state = State.PUSHING;
                        inMode = AcceptorModes.NONE;
                        outMode = AcceptorModes.PUSH;
                        transaction.commit();
                    }
                    else transaction.abort();
                }
            }
        }
        else if (state == State.PUSHING)
        {
            if (outputStorage.getAmount() == 0)
            {
                state = State.IDLE;
                inMode = AcceptorModes.PULL;
                outMode = AcceptorModes.NONE;
            }
        }
    }

    private AcceptorModes getInMode()
    {
        return inMode;
    }

    private AcceptorModes getOutMode()
    {
        return outMode;
    }

    public Storage<FluidVariant> getStorage(Direction direction)
    {
        Direction facing = getCachedState().get(FluidRationerBlock.FACING);
        if (direction == facing)
        {
            return outputStorage;
        }
        return null;
    }

    public FluidPump getPump(Direction direction)
    {
        Direction facing = getCachedState().get(FluidRationerBlock.FACING);
        if (direction == facing)
        {
            return outPump;
        }
        return null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        outputStorage.writeNbt(nbt);
        nbt.putInt("state", state.ordinal());
        nbt.putInt("inMode", inMode.ordinal());
        nbt.putInt("outMode", outMode.ordinal());
        nbt.putLong("targetAmount", targetAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        outputStorage.readNbt(nbt);
        this.state = State.values()[nbt.getInt("state")];
        this.inMode = AcceptorModes.values()[nbt.getInt("inMode")];
        this.outMode = AcceptorModes.values()[nbt.getInt("outMode")];
        this.targetAmount = nbt.getLong("targetAmount");
    }

    public void updateCache()
    {
        if (world instanceof ServerWorld serverWorld)
        {
            BlockPos behind = pos.offset(getCachedState().get(FluidRationerBlock.FACING).getOpposite());
            cache = BlockApiCache.create(FluidStorage.SIDED, serverWorld, behind);
        }
    }

    public enum State
    {
        IDLE,
        PUSHING
    }
}