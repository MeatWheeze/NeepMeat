package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import com.neep.neepmeat.fluid_util.FluidNode;
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

    private Map<Direction, FluidNode> sides = new HashMap<>();
    private Map<Direction, FluidAcceptor.AcceptorModes> sideModes = new HashMap<>();
    private FluidBuffer buffer;
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
                    FluidAcceptor.AcceptorModes mode = nodeProvider.getDirectionMode(state, direction);
                    sides.put(direction, new FluidNode(pos, direction, this.getBuffer(null), mode, mode.getFlow()));
                    sideModes.put(direction, mode);
                }
            }
        }
    }

    private int number = 7;

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity be)
    {
        if (be.needsUpdate)
        {
            be.update(be.getCachedState(), be.getWorld());
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

    public void setActive(boolean active)
    {
        if (active != isActive)
        {
            update(getCachedState(), getWorld());
        }
        if (!active)
        {
            sides.get(getCachedState().get(PumpBlock.FACING)).setMode(FluidAcceptor.AcceptorModes.NONE);
            sides.get(getCachedState().get(PumpBlock.FACING).getOpposite()).setMode(FluidAcceptor.AcceptorModes.NONE);
        }
        else
        {
            sides.get(getCachedState().get(PumpBlock.FACING)).setMode(FluidAcceptor.AcceptorModes.PUSH);
            sides.get(getCachedState().get(PumpBlock.FACING).getOpposite()).setMode(FluidAcceptor.AcceptorModes.PULL);
        }
        this.isActive = active;
    }

    public void update(BlockState state, World world)
    {
        sides.get(state.get(PumpBlock.FACING)).rebuildNetwork(world);
        sides.get(state.get(PumpBlock.FACING).getOpposite()).rebuildNetwork(world);
//        System.out.println(sides.values().forEach(););
//        sides.values().forEach(System.out::println);
        for (FluidNode node : sides.values())
        {
//            System.out.println(node.mode);
        }
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
        return sides.get(direction);
    }

    @Override
    @Nullable
    public FluidBuffer getBuffer(Direction direction)
    {
//        return sideModes.get(direction) != FluidAcceptor.AcceptorModes.NONE
//                || direction == null ? buffer : null;
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }
}
