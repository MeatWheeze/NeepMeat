package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.fluid_util.AcceptorModes;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.node.FluidNode;
import com.neep.neepmeat.fluid_util.NMFluidNetwork;
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
public class PumpBlockEntity extends BlockEntity implements FluidBufferProvider
{

    // Creates positive pressure at front, negative pressure at rear.
    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    private final Map<Direction, AcceptorModes > sideModes = new HashMap<>();
    private final FluidBuffer buffer;
    private NMFluidNetwork network;
    private boolean needsUpdate;
    private boolean isActive;

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);

        this.needsUpdate = true;
        buffer = new FluidBuffer(this, FluidConstants.BLOCK);
        // Create fluid interfaces in connection directions
        if (state.getBlock() instanceof FluidNodeProvider nodeProvider)
        {
            for (Direction direction : Direction.values())
            {
//                FluidNodeProvider nodeProvider = (FluidNodeProvider) state.getBlock();
                if (nodeProvider.connectInDirection(state, direction))
                {
                    AcceptorModes mode = nodeProvider.getDirectionMode(state, direction);
//                    sides.put(direction, new FluidNode(pos, direction, this.getBuffer(null), mode, mode.getFlow()));
                    sideModes.put(direction, mode);
                }
            }
        }
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
//        sides.clear();
//        update(getCachedState(), getWorld());
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
//            be.update(be.getCachedState(), be.getWorld());
        }
        if (!world.isReceivingRedstonePower(pos))
        {
            be.setActive(true);
//            be.sides.get(state.get(PumpBlock.FACING)).tick(world);
//            be.sides.get(state.get(PumpBlock.FACING).getOpposite()).tick(world);
        }
        else
        {
            be.setActive(false);
        }
    }

//    public void createNetwork()
//    {
//        new NMFluidNetwork(world, pos, getCachedState().get(PumpBlock.FACING));
//        new NMFluidNetwork(world, pos, getCachedState().get(PumpBlock.FACING).getOpposite());
//    }

    public void setActive(boolean active)
    {
        if (active != isActive)
        {
            update(getCachedState(), getWorld());
        }

        BlockState blockState = getCachedState();

        if (!active)
        {
            sideModes.replace(blockState.get(PumpBlock.FACING), AcceptorModes.NONE);
            sideModes.replace(blockState.get(PumpBlock.FACING).getOpposite(), AcceptorModes.NONE);
        }
        else
        {
            sideModes.replace(blockState.get(PumpBlock.FACING), AcceptorModes.PUSH);
            sideModes.replace(blockState.get(PumpBlock.FACING).getOpposite(), AcceptorModes.PULL);
        }
        this.isActive = active;
    }

    public void update(BlockState state, World world)
    {
//        sides.get(state.get(PumpBlock.FACING)).rebuildNetwork(world);
//        sides.get(state.get(PumpBlock.FACING).getOpposite()).rebuildNetwork(world);
//        createNetwork();
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
    public FluidBuffer getBuffer(Direction direction)
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
