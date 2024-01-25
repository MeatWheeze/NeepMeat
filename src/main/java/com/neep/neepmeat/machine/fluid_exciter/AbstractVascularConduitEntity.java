package com.neep.neepmeat.machine.fluid_exciter;

import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class AbstractVascularConduitEntity implements VascularConduitEntity
{
    private final BlockPos pos;
    @Nullable private BloodNetwork network;
    private boolean unloaded;

    public AbstractVascularConduitEntity(BlockPos pos)
    {
        this.pos = pos;
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
    }

    public void onRemove()
    {
        if (network != null && !unloaded)
        {
            network.remove(pos, this);
        }
    }

    public void onUnload()
    {
        if (network != null)
        {
            network.unload(pos, this);
            unloaded = true;
        }
    }

    @Override
    public BlockPos getBlockPos()
    {
        return pos;
    }
}
