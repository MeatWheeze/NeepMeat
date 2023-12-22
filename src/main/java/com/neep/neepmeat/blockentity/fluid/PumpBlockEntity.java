package com.neep.neepmeat.blockentity.fluid;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.transport.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.transport.block.fluid_transport.IVariableFlowBlock;
import com.neep.neepmeat.transport.block.fluid_transport.PumpBlock;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.FluidNetwork;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class PumpBlockEntity extends SyncableBlockEntity
{
    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    public final Map<Direction, AcceptorModes > sideModes = new HashMap<>();
    protected final WritableSingleFluidStorage buffer;
    private boolean isActive;

    public static final String FRONT_MODE = "front_mode";
    public static final String BACK_MODE = "back_mode";

    public AcceptorModes frontMode = AcceptorModes.NONE;
    public AcceptorModes backMode = AcceptorModes.NONE;

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
        if (active != isActive)
        {
            update(getCachedState(), getWorld());
        }

        BlockState blockState = getCachedState();

        Direction facing = blockState.get(PumpBlock.FACING);
        FluidNetwork.NodeSupplier node;
        NodePos front = new NodePos(getPos().offset(facing), facing.getOpposite());
        NodePos back = new NodePos(getPos().offset(facing.getOpposite()), facing);

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

        this.isActive = active;
        this.markDirty();
    }

    public void update(BlockState state, World world)
    {
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
        tag.putInt(FRONT_MODE, frontMode.getId());
       tag.putInt(BACK_MODE, backMode.getId());
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
        this.frontMode = AcceptorModes.byId(tag.getInt(FRONT_MODE));
        this.backMode = AcceptorModes.byId(tag.getInt(BACK_MODE));
    }

//    @Override
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

}
