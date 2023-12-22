package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.block.FluidNodeProvider;
import com.neep.neepmeat.block.PumpBlock;
import com.neep.neepmeat.fluid_util.FluidNode;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PumpBlockEntity extends BlockEntity implements Storage<FluidVariant>
{

    // Create positive pressure at front, negative pressure at rear.
    // When fluid storage is directly in front, redirect insertions to neighboring storage.

    private Map<Direction, FluidNode> sides = new HashMap<>();

    public PumpBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.PUMP_BLOCK_ENTITY, pos, state);

        // Create fluid interfaces in connection directions
        if (state.getBlock() instanceof FluidNodeProvider)
        {
            for (Direction direction : Direction.values())
            {
                FluidNodeProvider nodeProvider = (FluidNodeProvider) state.getBlock();
                if (nodeProvider.connectInDirection(state, direction))
                {
                    sides.put(direction, new FluidNode(pos, direction, this, nodeProvider.getDirectionMode(state, direction), 5));
                }
            }
        }
    }

    private int number = 7;

    public static void tick(World world, BlockPos pos, BlockState state, PumpBlockEntity be)
    {
        // TODO: work out why this could be null
//        be.sides.get(state.get(PumpBlock.FACING)).tick(world);
    }

    public void update(BlockState state, World world)
    {
        sides.get(state.get(PumpBlock.FACING)).rebuildNetwork(world);
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

    @Override
    public boolean supportsExtraction()
    {
        return true;
    }

    @Override
    public boolean supportsInsertion()
    {
        return true;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return null;
    }
}
