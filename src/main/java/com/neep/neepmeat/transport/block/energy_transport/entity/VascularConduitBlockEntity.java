package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.transport.api.BlockEntityUnloadListener;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class VascularConduitBlockEntity extends BlockEntity implements VascularConduitEntity, BlockEntityUnloadListener
{
    protected BloodNetwork network;

    public VascularConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
    }

    @Override
    public @Nullable BloodNetwork getNetwork()
    {
        return network;
    }

    @Override
    public void setNetwork(@Nullable BloodNetwork network)
    {
        this.network = network;
        markDirty();
    }

    private boolean unloaded;

    @Override
    public void markRemoved()
    {
        if (network != null && !unloaded)
        {
            // network.remove has some extra stuff for updating nearby acceptors.
            // If this happens when the world is unloading, it will cause the chunks to load again which
            // probably has unintended consequences.
            network.remove(pos, this);
        }
        super.markRemoved();
    }

    @Override
    public void onUnload(WorldChunk chunk)
    {
        if (network != null)
        {
            network.unload(pos, this);
            unloaded = true;
        }
    }
}
