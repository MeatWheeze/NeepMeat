package com.neep.neepmeat.machine.fluid_rationer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FluidRationerBlockEntity extends SyncableBlockEntity
{
    protected final WritableSingleFluidStorage outputStorage;
    protected final FluidPump inPump = FluidPump.of(-0.1f, this::getInMode, true);
    protected final FluidPump outPump = FluidPump.of(0.1f, this::getOutMode, true);

    protected AcceptorModes inMode = AcceptorModes.PULL;
    protected AcceptorModes outMode = AcceptorModes.NONE;

    protected State state;

    protected long takeAmount = 81000;

    public FluidRationerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.outputStorage = new WritableSingleFluidStorage(FluidConstants.BUCKET * 4, this::markDirty);
        this.state = State.PULLING;
    }

    public FluidRationerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLUID_RATIONER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FluidRationerBlockEntity be)
    {
        be.tick();
    }

    protected void tick()
    {
        if (state == State.PULLING)
        {
            if (outputStorage.getAmount() >= takeAmount)
            {
                state = State.PUSHING;
                inMode = AcceptorModes.NONE;
                outMode = AcceptorModes.PUSH;
            }
        }
        else if (state == State.PUSHING)
        {
            if (outputStorage.getAmount() == 0)
            {
                state = State.PULLING;
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
        else if (direction == facing.getOpposite())
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
        else if (direction == facing.getOpposite())
        {
            return inPump;
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
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        outputStorage.readNbt(nbt);
        this.state = State.values()[nbt.getInt("state")];
        this.inMode = AcceptorModes.values()[nbt.getInt("inMode")];
        this.outMode = AcceptorModes.values()[nbt.getInt("outMode")];
    }

    public enum State
    {
        PULLING,
        PUSHING
    }
}