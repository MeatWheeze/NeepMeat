package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.fluid_transport.PumpBlock;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class PumpBlockEntity extends SyncableBlockEntity
{
    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    protected final WritableSingleFluidStorage buffer;

    public static final String FRONT_MODE = "front_mode";
    public static final String BACK_MODE = "back_mode";

    public AcceptorModes frontMode = AcceptorModes.NONE;
    public AcceptorModes backMode = AcceptorModes.NONE;
    protected FluidPump frontPump = FluidPump.of(-0.5f, () -> frontMode, true);
    protected FluidPump backPump = FluidPump.of(0.5f, () -> backMode, true);

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.PUMP, pos, state);

        buffer = new WritableSingleFluidStorage(FluidConstants.BLOCK, this::sync);
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
    }

    public void setActive(boolean active)
    {
        if (!active)
        {
            frontMode = AcceptorModes.NONE;
            backMode = AcceptorModes.NONE;
        }
        else
        {
            frontMode = AcceptorModes.PUSH;
            backMode = AcceptorModes.PULL;
        }

        this.markDirty();
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
        tag.putInt(FRONT_MODE, frontMode.getId());
        tag.putInt(BACK_MODE, backMode.getId());
        tag.putBoolean("hasFrontStorage", hasFrontStorage);
        tag.putBoolean("hasRearStorage", hasRearStorage);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
        this.frontMode = AcceptorModes.byId(tag.getInt(FRONT_MODE));
        this.backMode = AcceptorModes.byId(tag.getInt(BACK_MODE));
        this.hasFrontStorage = tag.getBoolean("hasFrontStorage");
        this.hasRearStorage = tag.getBoolean("hasRearStorage");
    }

    @Nullable
    public SingleVariantStorage<FluidVariant> getBuffer(Direction direction)
    {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        if (direction == facing || direction == facing.getOpposite() || direction == null)
            return buffer;
        else
            return null;
    }

    @Override
    public void sync()
    {
        super.sync();
    }

    public FluidPump getPump(Direction direction)
    {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        return direction == facing ? frontPump : direction == facing.getOpposite() ? backPump : null;
    }

    BlockApiCache<Storage<FluidVariant>, Direction> frontCache;
    BlockApiCache<Storage<FluidVariant>, Direction> rearCache;
    protected boolean hasFrontStorage;
    protected boolean hasRearStorage;

    public void updateCache()
    {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        frontCache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, pos.offset(facing));
        rearCache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, pos.offset(facing.getOpposite()));

        Storage<FluidVariant> frontStorage = frontCache.find(world.getBlockState(pos.offset(facing)), facing);
        Storage<FluidVariant> rearStorage = rearCache.find(world.getBlockState(pos.offset(facing.getOpposite())), facing.getOpposite());
        hasFrontStorage = frontStorage != null;
        hasRearStorage = rearStorage != null;
    }

    // I resent making pumps ticked, it undermines the whole point of ticking fluid networks.
    public void tick()
    {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        // TODO: PLEASE CHANGE
        long transfer = FluidConstants.BUCKET / 8;

        // Create caches if they should be present
        if (hasFrontStorage && frontCache == null || hasRearStorage && rearCache == null)
            updateCache();

        if (rearCache != null && frontCache != null)
        {
            Storage<FluidVariant> frontStorage = frontCache.find(world.getBlockState(pos.offset(facing)), facing);
            Storage<FluidVariant> rearStorage = rearCache.find(world.getBlockState(pos.offset(facing.getOpposite())), facing.getOpposite());
            if (rearStorage != null && backMode.isDriving())
            {
                long tr1 = StorageUtil.move(rearStorage, buffer, v -> true, transfer, null);
            }
            if (frontStorage != null && frontMode.isDriving())
            {
                long tr2 = StorageUtil.move(buffer, frontStorage, v -> true, transfer, null);
            }
        }
    }
}
