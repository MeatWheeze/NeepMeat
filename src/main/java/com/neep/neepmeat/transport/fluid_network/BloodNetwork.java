package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface BloodNetwork
{
    void update(BlockPos pos, VascularConduitEntity.UpdateReason reason);
    void tick();
    void add(BlockPos pos, VascularConduitEntity newPart);
    void add(long pos, VascularConduitEntity newPart);
    void merge(List<BloodNetwork> adjNetworks);
    void mergeInto(BloodNetwork other);

    static BloodNetwork find(World world, BlockPos pos)
    {
        var conduit = VascularConduitEntity.LOOKUP.find(world, pos, null);
        if (conduit != null)
        {
            return conduit.getNetwork();
        }
        return null;
    }
}
