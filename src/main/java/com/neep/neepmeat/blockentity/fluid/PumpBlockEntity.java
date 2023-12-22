package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.block.fluid_transport.IFluidNodeProvider;
import com.neep.neepmeat.block.fluid_transport.PumpBlock;
import com.neep.neepmeat.fluid_transfer.AcceptorModes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.fluid_transfer.node.NodePos;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
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

    public static final String FRONT_MODE = "front_mode";
    public static final String BACK_MODE = "back_mode";

    public AcceptorModes frontMode = AcceptorModes.NONE;
    public AcceptorModes backMode = AcceptorModes.NONE;

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.PUMP_BLOCK_ENTITY, pos, state);

        buffer = new WritableFluidBuffer(this, FluidConstants.BLOCK);

        // Create fluid interfaces in connection directions
        if (state.getBlock() instanceof IFluidNodeProvider nodeProvider)
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
        this.markDirty();
    }

    public void update(BlockState state, World world)
    {
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
        tag.putInt(FRONT_MODE, frontMode.getId());
        tag.putInt(BACK_MODE, backMode.getId());
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
        this.frontMode = AcceptorModes.byId(tag.getInt(FRONT_MODE));
        this.backMode = AcceptorModes.byId(tag.getInt(BACK_MODE));
    }

    @Override
    @Nullable
    public WritableFluidBuffer getBuffer(Direction direction)
    {
        return buffer;
    }

}
