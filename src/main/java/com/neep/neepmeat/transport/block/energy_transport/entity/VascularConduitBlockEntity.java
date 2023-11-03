package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.transport.TransportComponents;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VascularConduitBlockEntity extends BlockEntity implements VascularConduitEntity
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
        register(world);
    }

    @Override
    public BloodNetwork getNetwork()
    {
        return network;
    }

    @Override
    public void setNetwork(BloodNetwork network)
    {
        this.network = network;
    }

    @Override
    public void markRemoved()
    {
        if (network != null)
        {
            network.remove(pos, this);
        }
    }
}
