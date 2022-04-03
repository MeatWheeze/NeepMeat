package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.fluid_transfer.node.FluidNode;
import com.neep.neepmeat.fluid_transfer.NMFluidNetwork;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class PumpBlockEntity extends BlockEntity implements com.neep.neepmeat.fluid_transfer.FluidBuffer.FluidBufferProvider
{

    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    private final Map<Direction, AcceptorModes > sideModes = new HashMap<>();
    private final WritableFluidBuffer buffer;
    private NMFluidNetwork network;
    private boolean needsUpdate;
    private boolean isActive;

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);

        this.needsUpdate = true;
        buffer = new WritableFluidBuffer(this, FluidConstants.BLOCK);

        // Create fluid interfaces in connection directions
        if (state.getBlock() instanceof FluidNodeProvider nodeProvider)
        {
            for (Direction direction : Direction.values())
            {
                if (nodeProvider.connectInDirection(state, direction))
                {
                    AcceptorModes mode = nodeProvider.getDirectionMode(state, direction);
                    sideModes.put(direction, mode);
                }
            }
        }
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

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity be)
    {
        if (be.isRemoved())
        {
            be.setNeedsUpdate(true);
        }
        if (be.needsUpdate)
        {
        }
        if (!world.isReceivingRedstonePower(pos))
        {
            be.setActive(true);
        }
        else
        {
            be.setActive(false);
        }
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
            sideModes.replace(facing, AcceptorModes.NONE);
            sideModes.replace(facing.getOpposite(), AcceptorModes.NONE);

            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(front)).exists())
            {
                node.get().setMode(AcceptorModes.NONE);
            }
            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(back)).exists())
            {
                node.get().setMode(AcceptorModes.NONE);
            }
        }
        else
        {
            sideModes.replace(facing, AcceptorModes.PUSH);
            sideModes.replace(facing.getOpposite(), AcceptorModes.PULL);

            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(front)).exists())
            {
                node.get().setMode(AcceptorModes.PUSH);
            }
            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(back)).exists())
            {
                node.get().setMode(AcceptorModes.PULL);
            }

        }

        this.isActive = active;
    }

    public void update(BlockState state, World world)
    {
        needsUpdate = false;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNBT(tag);
    }

    public FluidNode getNode(Direction direction)
    {
        Direction facing = getCachedState().get(PumpBlock.FACING);
        return new FluidNode(pos.offset(direction.getOpposite()), direction.getOpposite(), this.getBuffer(direction), sideModes.get(direction.getOpposite()), 2);
    }

    @Override
    @Nullable
    public WritableFluidBuffer getBuffer(Direction direction)
    {
//        return sideModes.get(direction) != FluidAcceptor.AcceptorModes NONE
//                || direction == null ? buffer : null;
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }
}
