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

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);

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
                    sides.put(direction, new FluidNode(pos, direction, this.getBuffer(null), mode, mode.getPressure()));
                    sideModes.put(direction, nodeProvider.getDirectionMode(state, direction));
                }
            }
        }
    }

    private int number = 7;

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity be)
    {
//        be.sides.get(state.get(PumpBlock.FACING)).tick(world);
//        be.sides.get(state.get(PumpBlock.FACING).getOpposite()).tick(world);
    }

    public void update(BlockState state, World world)
    {
        sides.get(state.get(PumpBlock.FACING)).rebuildNetwork(world);
        sides.get(state.get(PumpBlock.FACING).getOpposite()).rebuildNetwork(world);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("number", number);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        number = tag.getInt("number");
    }

    public FluidNode getNode(Direction direction)
    {
        return sides.get(direction);
    }

//    @Override
//    public boolean supportsExtraction()
//    {
//        return true;
//    }
//
//    @Override
//    public boolean supportsInsertion()
//    {
//        return true;
//    }

//    @Override
//    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
//    {
//        return maxAmount;
//    }
//
//    @Override
//    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
//    {
//        return maxAmount;
//    }
//
//    @Override
//    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
//    {
//        return null;
//    }

    @Override
    @Nullable
    public FluidBuffer getBuffer(Direction direction)
    {
//        return sideModes.get(direction) != FluidAcceptor.AcceptorModes.NONE
//                || direction == null ? buffer : null;
        return buffer;
    }
}
