package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.fluid_network.BloodNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class VascularConduitBlockEntity extends BlockEntity implements VascularConduitEntity
{
    protected BloodNetwork network;

    public VascularConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public @NotNull BloodNetwork getNetwork()
    {
        return network;
    }

    @Override
    public void setNetwork(@NotNull BloodNetwork network)
    {
        this.network = network;
    }
}
