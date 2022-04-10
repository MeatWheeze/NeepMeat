package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
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

@SuppressWarnings("UnstableApiUsage")
public class PumpBlockEntity extends BlockEntity implements com.neep.neepmeat.fluid_transfer.FluidBuffer.FluidBufferProvider
{
    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    public final Map<Direction, AcceptorModes > sideModes = new HashMap<>();
    protected final WritableFluidBuffer buffer;
    private boolean isActive;

    public AcceptorModes frontMode = AcceptorModes.PUSH;
    public AcceptorModes backMode = AcceptorModes.PULL;

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);

        buffer = new WritableFluidBuffer(this, FluidConstants.BLOCK);

        // Create fluid interfaces in connection directions
        if (state.getBlock() instanceof FluidNodeProvider nodeProvider)
        {
            for (Direction direction : Direction.values())
            {
                if (nodeProvider.connectInDirection(world, pos, state, direction))
                {
//                    AcceptorModes mode = nodeProvider.getDirectionMode(world, pos, state, direction);
//                    sideModes.put(direction, mode);
//                    setActive(false);
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
//            sideModes.replace(facing, AcceptorModes.NONE);
//            sideModes.replace(facing.getOpposite(), AcceptorModes.NONE);

            frontMode = AcceptorModes.NONE;
            backMode = AcceptorModes.NONE;

//            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(front)).exists())
//            {
//                node.get().setMode(AcceptorModes.NONE);
//            }
//            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(back)).exists())
//            {
//                node.get().setMode(AcceptorModes.NONE);
//            }
        }
        else
        {
//            sideModes.replace(facing, AcceptorModes.PUSH);
//            sideModes.replace(facing.getOpposite(), AcceptorModes.PULL);

            frontMode = AcceptorModes.PUSH;
            backMode = AcceptorModes.PULL;

//            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(front)).exists())
//            {
//                node.get().setMode(AcceptorModes.PUSH);
//            }
//            if ((node = FluidNetwork.getInstance(world).getNodeSupplier(back)).exists())
//            {
//                node.get().setMode(AcceptorModes.PULL);
//            }

        }

        this.isActive = active;
    }

    public void update(BlockState state, World world)
    {
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
    }
}
